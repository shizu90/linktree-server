	package com.shizu.linktree.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shizu.linktree.entities.LinkTree;
import com.shizu.linktree.entities.User;
import com.shizu.linktree.services.LinkTreeService;
import com.shizu.linktree.services.UserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/linktree")
public class LinkTreeController {
	@Autowired
	private LinkTreeService service;
	@Autowired
	private UserService userService;
	
	@GetMapping(value = "/{username}")
	public ResponseEntity<LinkTree> getLinkTree(@PathVariable String username) {
		User user = userService.findByUsername(username);
		LinkTree linkTree = service.findByUser(user);
		return ResponseEntity.ok().body(linkTree);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteLinkTree(@PathVariable Long id, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		User user = userService.findById(token);
		service.delete(user.getLinkTree().getId() | id);
		return ResponseEntity.ok().body(null);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> updateLinkTree(@PathVariable Long id, @RequestBody LinkTree linkTree, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		User user = userService.findById(token);
		service.update(user.getLinkTree().getId(), linkTree);
		return ResponseEntity.ok().body(null);
	}
}
