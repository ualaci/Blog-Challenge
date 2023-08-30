package com.blogchallenge.blogchallenge.controllers;
import com.blogchallenge.blogchallenge.entities.Comment;
import com.blogchallenge.blogchallenge.entities.History;
import com.blogchallenge.blogchallenge.entities.Post;
import com.blogchallenge.blogchallenge.services.PostServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PostController {



    private final PostServiceImpl postService;

    public PostController(PostServiceImpl postService) {
        this.postService = postService;
    }

    @PostMapping("/posts/{postId}")
    public ResponseEntity<?> processPost(@PathVariable Long postId) {
        Optional<Post> post = Optional.empty();

        if(postService.findPost(postId).isEmpty()){
            post=postService.processPost(postId);
            return ResponseEntity.status(HttpStatus.OK).body(post);
        }

        post = postService.findPost(postId);
        List<History> history = post.get().getHistory();
        History lastHistory = null;
        if (!history.isEmpty()) lastHistory = history.get(history.size() - 1);

        switch (Objects.requireNonNull(lastHistory).getState()) {
            case CREATED -> {
                postService.findPAndUpdatePost(postId);
                return ResponseEntity.status(HttpStatus.OK).body(post);
            }
            case POST_FIND -> {
                postService.setOKPost(postId);
                return ResponseEntity.status(HttpStatus.OK).body(post);
            }
            case POST_OK -> {
                postService.findAndUpdateComments(postId);
                return ResponseEntity.status(HttpStatus.OK).body(post);
            }
            case COMMENTS_FIND -> {
                postService.setOKComments(postId);
                return ResponseEntity.status(HttpStatus.OK).body(post);
            }
            case COMMENTS_OK -> {
                postService.enablePost(postId);
                return ResponseEntity.status(HttpStatus.OK).body(post);
            }
            case ENABLED -> {
                return ResponseEntity.status(HttpStatus.OK).body("Post is Enabled");
            }
            case DISABLED -> {
                return ResponseEntity.status(HttpStatus.OK).body("Post is Disabled");
            }
            case FAILED -> {
                postService.disablePost(postId);
                return ResponseEntity.status(HttpStatus.OK).body(post);
            }
            case UPDATING -> {
                postService.reprocessPost(postId);
                ResponseEntity.status(HttpStatus.OK).body(post);
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(post);

    }

    @GetMapping("/postsFromClient")
    public List<Post> getAllPostsFromClient() {
        return postService.getAllPostsFromClient();
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts(
    @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
    @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
    @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
    @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {

        Optional<List<Post>> post = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<Comment> getCommentsForPostOnClient(@PathVariable Long postId) {
        return postService.getCommentsForPostOnClient(postId);
    }

    @GetMapping("/posts/{postId}")
    public Post getPostById(@PathVariable Long postId) {
        return postService.getPost(postId).orElseThrow(
                () -> new RuntimeException("Post not found")
        );
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> reprocessPost(@PathVariable Long postId) {
        Optional<Post> postOptional = postService.reprocessPost(postId);
        Post post = postOptional.orElse(null);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> disablePost(@PathVariable Long postId) {
        Optional<Post> optionalPost = postService.disablePost(postId);
        Post post = optionalPost.orElse(null);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }
}
