package com.shizu.linktree.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.shizu.linktree.entities.User;
import com.shizu.linktree.entities.dto.RegisterDTO;
import com.shizu.linktree.repositories.UserRepository;
import com.shizu.linktree.services.exception.AlreadyExistsException;
import com.shizu.linktree.services.exception.DatabaseException;
import com.shizu.linktree.services.exception.InvalidFormatException;
import com.shizu.linktree.services.exception.ResourceNotFoundException;
import com.shizu.linktree.services.exception.UnauthorizedException;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repo;
	private PasswordEncoder passwordEncoder;
	@Value("${TOKEN_PASSWORD}")
	private String TOKEN_PASSWORD;
	
	public UserService() {
		this.passwordEncoder = new BCryptPasswordEncoder();
	}
	
	public List<User> findAll() {
		return repo.findAll();
	}
	
	public User findById(Long id) {
		Optional<User> user = repo.findById(id);
		return user.orElseThrow(() -> new ResourceNotFoundException("User not found."));
	}
	
	public User findByUsername(String username) {
		if(repo.findByUsername(username) != null) {
			return repo.findByUsername(username).get(0);
		}else {
			throw new ResourceNotFoundException("User not found.");
		}
	}
	
	public User insert(RegisterDTO credentials) {
		if(repo.findByEmail(credentials.getEmail()).isEmpty() && repo.findByUsername(credentials.getUsername()).isEmpty()) {
			if(credentials.getPassword().length() < 6) throw new InvalidFormatException("Invalid password format.");
			if(credentials.getUsername().length() < 4) throw new InvalidFormatException("Invalid username format.");
			if(!credentials.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) throw new InvalidFormatException("Invalid email format.");
			if(!credentials.getConfirmPassword().matches(credentials.getPassword())) throw new InvalidFormatException("Passwords mismatch.");
		}else {
			throw new AlreadyExistsException("Email or username already exists.");
		}
		try {
			credentials.setPassword(encryptPassword(credentials.getPassword()));
			User user = new User(null, credentials.getUsername(), credentials.getEmail(), credentials.getPassword(), "", "", null);
			return repo.save(user);
		}catch(ConstraintViolationException e) {
			throw new AlreadyExistsException("Email or username already exists.");
		}
	}
	
	public void delete(Long id, String token) {
		String decodedToken = decodeJwtToken(token);
		try {
			repo.deleteById(Long.parseLong(decodedToken));
		}catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Resource with specified id doesn't exists: " + id);
		}catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Can't delete an user that have an linktree. Delete the user's linktree first.");
		}
	}
	
	public void update(User obj, Long id) {
		try {
			User user = findById(id);
			updateData(user, obj);
			repo.save(user);
		}catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("User not found.");
		}
	}
	
	private void updateData(User user, User obj) {
		if(obj.getUsername() != null && repo.findByUsername(obj.getUsername()).isEmpty()) {
			user.setUsername(obj.getUsername());
		}else throw new AlreadyExistsException("Username already taken.");
		if(obj.getEmail() != null && repo.findByEmail(obj.getEmail()).isEmpty()) {
			user.setEmail(obj.getEmail());
		}else throw new AlreadyExistsException("Email already on use.");
		if(obj.getPassword() != null) user.setPassword(encryptPassword(obj.getPassword()));
		if(obj.getDescription() != null) user.setDescription(obj.getDescription());
	}
	
	public String login(RegisterDTO credentials) {
		List<User> res = repo.findByEmail(credentials.getEmail());
		if(res.size() >= 1) {
			User user = res.get(0);
			if(passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
				return encodeJwt(user.getId().toString(), 2_600_000);
			}else {
				throw new ResourceNotFoundException("Invalid password.");
			}
		}else {
			throw new ResourceNotFoundException("Email not found.");
		}
	}
	
	
	public String decodeJwtToken(String token) {
		try {
			String decodedToken = JWT.require(Algorithm.HMAC512(TOKEN_PASSWORD)).build().verify(token).getSubject();
			return decodedToken;
		}catch(TokenExpiredException e) {
			throw new UnauthorizedException("Token was expired");
		}catch(JWTDecodeException e) {
			throw new UnauthorizedException("Invalid token format");
		}
	}
	
	public String encodeJwt(String subject, Integer expirationTime) {
		String token = JWT.create().withSubject(subject).withExpiresAt(new Date(System.currentTimeMillis() + expirationTime)).sign(Algorithm.HMAC512(TOKEN_PASSWORD));
		return token;
	}
	
	public String encryptPassword(String password) {
		String encodedPassword = passwordEncoder.encode(password);
		return encodedPassword;
	}
}
