package com.shizu.linktree.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shizu.linktree.entities.Link;
import com.shizu.linktree.entities.User;
import com.shizu.linktree.services.LinkService;
import com.shizu.linktree.services.UserService;

@RestController
@RequestMapping(value = "/link")
public class LinkController {
	@Autowired
	private LinkService service;
	@Autowired
	private UserService userService;
	
	@PostMapping(value = "/{id}")
	public ResponseEntity<Link> postLink(@PathVariable Long id, @RequestBody Link link, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		User user = userService.findById(token);
		link.setLinkTree(user.getLinkTree());
		service.insert(link);
		return ResponseEntity.ok().body(link);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteLink(@PathVariable Long id, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		if(userService.decodeJwtToken(token) != null) {
			service.delete(id);
		}
		return ResponseEntity.ok().body(null);
	}
}
