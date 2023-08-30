package com.blogchallenge.blogchallenge.repositories;

import com.blogchallenge.blogchallenge.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
