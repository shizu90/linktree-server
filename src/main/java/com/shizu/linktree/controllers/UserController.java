package com.shizu.linktree.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shizu.linktree.entities.User;
import com.shizu.linktree.entities.dto.RegisterDTO;
import com.shizu.linktree.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserController {
	@Autowired
	private UserService service;
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<User> getUser(@PathVariable Long id, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		User user = service.findById(token);
		return ResponseEntity.ok().body(user);
	}
	
	@PostMapping
	public ResponseEntity<User> postUser(@RequestBody RegisterDTO credentials) {
		User obj = service.insert(credentials);
		return ResponseEntity.ok().body(obj);
	}
	
	@PostMapping(value = "/login")
	public ResponseEntity<String> postLogin(@RequestBody RegisterDTO credentials) {
		String token = service.login(credentials);
		return ResponseEntity.ok().body(token);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		service.delete(id, token);
		return ResponseEntity.ok().body(null);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody User user, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		service.update(user, token);
		return ResponseEntity.ok().body(null);
	}
	
}
