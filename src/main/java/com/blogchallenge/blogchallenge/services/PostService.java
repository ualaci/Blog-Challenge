package com.blogchallenge.blogchallenge.services;

import com.blogchallenge.blogchallenge.entities.Comment;
import com.blogchallenge.blogchallenge.entities.Post;

import java.util.List;

public interface PostService {
    public List<Post> getAllPostsFromClient();

    public Post getPostByIdFromClient(Long postId);

    public List<Comment> getCommentsForPostOnClient(Long postId);

}
