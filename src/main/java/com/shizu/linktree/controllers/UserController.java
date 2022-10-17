package com.shizu.linktree.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shizu.linktree.entities.LinkTree;
import com.shizu.linktree.entities.User;
import com.shizu.linktree.entities.dto.RegisterDTO;
import com.shizu.linktree.services.LinkTreeService;
import com.shizu.linktree.services.S3Service;
import com.shizu.linktree.services.UserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/users")
public class UserController {
	@Autowired
	private UserService service;
	@Autowired
	private LinkTreeService linkTreeService;
	@Autowired
	private S3Service s3Service;
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<User> getUser(@PathVariable Long id, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		User user = service.findById(token);
		return ResponseEntity.ok().body(user);
	}
	
	@GetMapping(value = "/validate/{token}")
	public ResponseEntity<String> validateJwt(@PathVariable String token) {
		String subject = service.decodeJwtToken(token);
		return ResponseEntity.ok().body(subject);
	}
	
	@PostMapping
	public ResponseEntity<User> postUser(@RequestBody RegisterDTO credentials) {
		User obj = service.insert(credentials);
		linkTreeService.insert(new LinkTree(null, obj, null, "originalDark"));
		return ResponseEntity.ok().body(obj);
	}
	
	@PostMapping(value = "/login")
	public ResponseEntity<String> postLogin(@RequestBody RegisterDTO credentials) {
		String token = service.login(credentials);
		return ResponseEntity.ok().body(token);
	}
	
	@PostMapping(value = "/uploadImg")
	public ResponseEntity<String> postImg(@RequestParam(name = "file") MultipartFile file, @RequestHeader(value = "authorization", defaultValue ="") String token) throws IOException {
		String url = s3Service.uploadFile(token, file);
		User user = service.findById(token);
		linkTreeService.update(user.getLinkTree().getId(), new LinkTree(user.getLinkTree().getId(), user, null, user.getLinkTree().getColorTheme()));
		return ResponseEntity.ok().body(url);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		service.delete(id, token);
		return ResponseEntity.ok().body(null);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody User user, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		User userObj = service.findById(token);
		service.update(user, token);
		linkTreeService.update(id, new LinkTree(userObj.getLinkTree().getId(), userObj, null, userObj.getLinkTree().getColorTheme()));
		return ResponseEntity.ok().body(null);
	}
	
}
