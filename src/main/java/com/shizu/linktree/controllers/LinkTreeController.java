	package com.shizu.linktree.controllers;

import java.io.IOException;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shizu.linktree.entities.LinkTree;
import com.shizu.linktree.entities.User;
import com.shizu.linktree.services.LinkTreeService;
import com.shizu.linktree.services.S3Service;
import com.shizu.linktree.services.UserService;

@RestController
@RequestMapping(value = "/linktree")
public class LinkTreeController {
	@Autowired
	private LinkTreeService service;
	@Autowired
	private UserService userService;
	@Autowired
	private S3Service s3Service;
	
	@GetMapping(value = "/{username}")
	public ResponseEntity<LinkTree> getLinkTree(@PathVariable String username) {
		User user = userService.findByUsername(username);
		LinkTree linkTree = service.findByUser(user);
		return ResponseEntity.ok().body(linkTree);
	}
	
	@PostMapping(value = "/{id}")
	public ResponseEntity<LinkTree> postLinkTree(@PathVariable Long id, @RequestBody LinkTree linkTree, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		User user = userService.findById(token);
		linkTree.setUser(user);
		service.insert(linkTree);
		return ResponseEntity.ok().body(linkTree);
	}
	
	@PostMapping(value = "/upload/{id}")
	public ResponseEntity<String> postUpload(@PathVariable Long id, @RequestParam(name = "file") MultipartFile file) throws IOException{
		String url = s3Service.uploadFile(id, file);
		return ResponseEntity.ok().body(url);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteLinkTree(@PathVariable Long id, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		User user = userService.findById(userService.decodeJwtToken(token));
		service.delete(user.getLinkTree().getId());
		return ResponseEntity.ok().body(null);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> updateLinkTree(@PathVariable Long id, @RequestBody LinkTree linkTree, @RequestHeader(value = "authorization", defaultValue ="") String token) {
		User user = userService.findById(token);
		service.update(user.getLinkTree().getId(), linkTree);
		return ResponseEntity.ok().body(null);
	}
}
