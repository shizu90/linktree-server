package com.shizu.linktree.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shizu.linktree.entities.Link;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {}
