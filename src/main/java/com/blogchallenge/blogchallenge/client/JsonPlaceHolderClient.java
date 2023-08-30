package com.blogchallenge.blogchallenge.client;
import com.blogchallenge.blogchallenge.entities.Comment;
import com.blogchallenge.blogchallenge.entities.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Component
@FeignClient(value = "posts", url = "https://jsonplaceholder.typicode.com/posts")
public interface JsonPlaceHolderClient {

    @GetMapping(value = "/{id}")
    Post getPost(@PathVariable("id") Long id);

    @GetMapping
    List<Post> getPosts();

    @GetMapping
    List<Comment> getPostComments();

    @GetMapping(value = "/{id}/comments")
    List<Comment> getComments(@PathVariable("id") Long id);
}