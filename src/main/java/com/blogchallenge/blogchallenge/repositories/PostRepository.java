package com.blogchallenge.blogchallenge.repositories;

import com.blogchallenge.blogchallenge.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long>{
}
