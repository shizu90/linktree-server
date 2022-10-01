package com.shizu.linktree.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.shizu.linktree.entities.LinkTree;
import com.shizu.linktree.entities.User;
import com.shizu.linktree.repositories.LinkTreeRepository;

@Service
public class LinkTreeService {
	@Autowired
	private LinkTreeRepository repo;
	
	public LinkTree findById(Long id) {
		Optional<LinkTree> linkTree = repo.findById(id);
		return linkTree.orElseThrow(() -> new RuntimeException());
	}
	
	public LinkTree findByUser(User user) {
		LinkTree linkTree = repo.findByUser(user).get(0);
		return linkTree;
	}
	public LinkTree insert(LinkTree linkTree) {
		return repo.save(linkTree);
	}
	
	public void delete(Long id) {
		try {
			repo.deleteById(id);
		}catch(EmptyResultDataAccessException e) {
			throw new RuntimeException();
		}catch(DataIntegrityViolationException e) {
			throw new RuntimeException();
		}
	}
	
	public void update(Long id, LinkTree obj) {
		LinkTree linkTree = findById(id);
		updateData(linkTree, obj);
		repo.save(linkTree);
	}
	
	public void updateData(LinkTree linkTree, LinkTree obj) {
		linkTree.setColorTheme(obj.getColorTheme());
		linkTree.setDescription(obj.getDescription());
		linkTree.setUsername(obj.getUsername());
		linkTree.setUserImg(obj.getUserImg());
	}
}
