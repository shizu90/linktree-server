package com.shizu.linktree.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.shizu.linktree.entities.Link;
import com.shizu.linktree.repositories.LinkRepository;

@Service
public class LinkService {
	
	@Autowired
	private LinkRepository repo;
	
	public Link findById(Long id) {
		Optional<Link> link = repo.findById(id);
		return link.orElseThrow(() -> new RuntimeException());
	}
	
	public Link insert(Link link) {
		return repo.save(link);
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
	
	public void update(Long id, Link obj) {
		Link link = findById(id);
		updateData(link, obj);
		repo.save(link);
	}
	
	public void updateData(Link link, Link obj) {
		link.setName(obj.getName());
		link.setUrl(obj.getUrl());
	}
}
