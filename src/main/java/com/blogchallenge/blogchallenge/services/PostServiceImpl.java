package com.blogchallenge.blogchallenge.services;

import com.blogchallenge.blogchallenge.client.JsonPlaceHolderClient;
import com.blogchallenge.blogchallenge.entities.Comment;
import com.blogchallenge.blogchallenge.entities.History;
import com.blogchallenge.blogchallenge.entities.Post;
import com.blogchallenge.blogchallenge.entities.PostStates;
import com.blogchallenge.blogchallenge.exceptions.BlogAPIException;
import com.blogchallenge.blogchallenge.exceptions.FailedException;
import com.blogchallenge.blogchallenge.repositories.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final JsonPlaceHolderClient jsonPlaceholderClient;
    private final PostRepository postRepository;



    public PostServiceImpl(JsonPlaceHolderClient jsonPlaceHolderClient, PostRepository postRepository) {
        this.jsonPlaceholderClient= jsonPlaceHolderClient;
        this.postRepository = postRepository;
    }

    public Optional<Post> processPost(Long id) {
        if (id > 100) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Post id must be less than 100!");
        }

        Post post = jsonPlaceholderClient.getPost(id);
        post.setReprocessed(false);
        post.setCreationDate(java.time.LocalDateTime.now());
        History history = History.builder()
                .post(post)
                .processedDate(java.time.LocalDateTime.now())
                .state(PostStates.CREATED)
                .build();

        System.out.println(history.toString());
        post.addHistory(history);
        return Optional.of(postRepository.save(post));
    }

    private History createHistory(Post post, PostStates state){
        return History.builder()
                .post(post)
                .processedDate(java.time.LocalDateTime.now())
                .state(state)
                .build();
    }

    public Optional<Post> findPAndUpdatePost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        Post post = null;
        if (optionalPost.isEmpty()){
            post = Post.builder()
                    .id(id)
                    .isReprocessed(false)
                    .history(List.of(createHistory(post, PostStates.FAILED)))
                    .build();
            postRepository.save(post);
        }
        post = optionalPost.orElseThrow(()-> new FailedException("Post","id", id));
        post.setCreationDate(java.time.LocalDateTime.now());
        post.getHistory().add(createHistory(post, PostStates.POST_FIND));
        return Optional.of(postRepository.save(post));
    }

    public Optional<Post> findPost(Long id){
        return postRepository.findById(id);
    }

    public Optional<Post> setOKPost(Long id){
        Optional<Post> optionalPost = postRepository.findById(id);
        Post post = optionalPost.orElse(null);
        if(post.getHistory().isEmpty()){
            post.addHistory(createHistory(post, PostStates.FAILED));
            return Optional.of(postRepository.save(post));
        }
        post.addHistory(createHistory(post, PostStates.POST_OK));
        return Optional.of(postRepository.save(post));
    }

    public Optional<Post> findAndUpdateComments(Long id){
        Optional<Post> optionalPost = postRepository.findById(id);
        Post post = optionalPost.orElse(null);
        Optional<List<Comment>> optionalComments = Optional.of(jsonPlaceholderClient.getComments(id));
        if (optionalComments.isEmpty()){
            post.addHistory(createHistory(post, PostStates.FAILED));
            return Optional.of(postRepository.save(post));
        }
        List<Comment> comments = optionalComments.orElseThrow(()-> new FailedException("Comments from Post","id", id));
        comments.forEach(comment -> comment.setPost(post));
        post.setComments(comments);
        post.addHistory(createHistory(post, PostStates.COMMENTS_FIND));
        return Optional.of(postRepository.save(post));
    }

    public Optional<Post> setOKComments(Long id){
        Optional<Post> optionalPost = postRepository.findById(id);
        Post post = optionalPost.orElse(null);
        post.addHistory(createHistory(post, PostStates.COMMENTS_OK));
        return Optional.of(postRepository.save(post));
    }

    public Optional<Post> enablePost(Long id){
        Optional<Post> optionalPost = postRepository.findById(id);
        Post post = optionalPost.orElse(null);
        post.addHistory(createHistory(post, PostStates.ENABLED));
        return Optional.of(postRepository.save(post));
    }

    public Optional<Post> disablePost(Long id){
        if (id > 100) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Post id must be less than 100!");
        }
        Optional<Post> optionalPost = postRepository.findById(id);
        Post post = optionalPost.orElseThrow(()-> new FailedException("Post","id", id));

        History lastHistory = post.getHistory().get(post.getHistory().size()-1);
        if(!(lastHistory.getState().equals(PostStates.FAILED)) && !(lastHistory.getState().equals(PostStates.ENABLED))){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Post must be FAILED or ENABLED to be disabled!");
        }
        post.addHistory(createHistory(post, PostStates.DISABLED));
        return Optional.of(postRepository.save(post));
    }

    /*public Optional<Post> reprocessPost(Long id){
        Optional<Post> optionalPost = postRepository.findById(id);
        Post clientPost = getPostByIdFromClient(id);
        Post post = optionalPost.orElseThrow(()-> new FailedException("Post","id", id));
        History lastHistory = post.getHistory().get(post.getHistory().size()-1);
        if(!(lastHistory.getState().equals(PostStates.DISABLED)) && !(lastHistory.getState().equals(PostStates.ENABLED))){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Post must be DISABLED or ENABLED to be updated!");
        }
        //Create a new post with the updated data from client, but keeps the history from the old post
        Post newPost = Post.builder()
                .id(clientPost.getId())
                .title(clientPost.getTitle())
                .userId(clientPost.getUserId())
                .creationDate(post.getCreationDate())
                .comments(findAndUpdateComments(id).orElseThrow(()-> new FailedException("Comments from Post","id", id)).getComments())
                .history(post.getHistory())
                .isReprocessed(true)
                .build();

        post.addHistory(createHistory(post, PostStates.POST_FIND));
        return Optional.of(postRepository.save(newPost));
    }*/

    public Optional<Post> reprocessPost(Long id){
        if (id > 100) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Post id must be less than 100!");
        }
        Optional<Post> optionalPost = postRepository.findById(id);
        Post post = optionalPost.orElseThrow(()-> new FailedException("Post","id", id));
        History lastHistory = post.getHistory().get(post.getHistory().size()-1);
        if(!(lastHistory.getState().equals(PostStates.DISABLED)) && !(lastHistory.getState().equals(PostStates.ENABLED))){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST,"Post must be DISABLED or ENABLED to be updated!");
        }
        post.setReprocessed(true);
        post.setHistory(new ArrayList<>());
        post.setComments(new ArrayList<>());
        post.addHistory(createHistory(post, PostStates.UPDATING));
        return Optional.of(postRepository.save(post));
    }

    public Optional<List<Post>> getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return Optional.ofNullable(Optional.of(postRepository.findAll(pageable).getContent())
                .orElseThrow(() -> new BlogAPIException(HttpStatus.NOT_FOUND, "No posts found!")));
    }

    public List<Post> getAllPostsFromClient() {
        return jsonPlaceholderClient.getPosts();
    }

    public List<Comment> getCommentsForPostOnClient(Long postId) {
        return jsonPlaceholderClient.getPostComments();
    }
    public Post getPostByIdFromClient(Long postId) {
        return jsonPlaceholderClient.getPost(postId);
    }

    public Optional<Post> getPost(Long postId) {
        return postRepository.findById(postId);
    }


}
