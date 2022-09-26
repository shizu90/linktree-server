package com.shizu.linktree.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shizu.linktree.entities.LinkTree;
import com.shizu.linktree.entities.User;

@Repository
public interface LinkTreeRepository extends JpaRepository<LinkTree, Long> {
	List<LinkTree>findByUser(User user);
}
