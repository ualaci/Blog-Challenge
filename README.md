# CHALLENGE 3

## Objective

To create an application that asynchronously fetches posts from an external API, enriches them with comment data, and keeps a log of processing updates. The client will then be able to search for posts and the history of states through the API immediately.

## Technical Requirements

- The app must run on port 8080.
- The database must be an embedded H2 database.
- The Spring configuration spring.jpa.hibernate.ddl-auto should be set to create-drop.
- If there is a message broker, it should be embedded as well.

## Evaluation

The app will be subjected to a bombardment of requests simulating a high volume of processing, and it is expected that the responses and processing will be as close to real-time as possible. The evaluation will be based primarily on the objective and technical requirements. Additionally, the quality of the code, chosen architecture, and the resilience of the solution will be assessed.

## External Data Source

URL: [https://jsonplaceholder.typicode.com](https://jsonplaceholder.typicode.com/)

## States of Posts

- CREATED: Initial state of a new post.
- POST_FIND: Indicates that the app is searching for basic post data.
- POST_OK: Indicates that the basic post data is already available.
- COMMENTS_FIND: Indicates that the app is searching for post comments.
- COMMENTS_OK: Indicates that the post comments are already available.
- ENABLED: Indicates that the post has been successfully processed and is enabled.
- DISABLED: Indicates that the post is disabled, either due to a processing failure or by user decision.
- UPDATING: Indicates that the post needs to be reprocessed.
- FAILED: Indicates a processing error.

The states of posts can only transition to other states as shown in the image below:

![image1.png](https://github.com/ualaci/Blog-Challenge/blob/1eb073d539749f7ee0471b73336428bbd13246d2/Image1.png)


## API / Features

1. Process Post
    - Description: Processes a post.
    - Method: POST
    - Path: /posts/{postId}
    - Requirements:
        - postId must be a number between 1 and 100.
        - Existing postId should not be accepted.
2. Disable Post
    - Description: Disables a post that is in the "ENABLED" state.
    - Method: DELETE
    - Path: /posts/{postId}
    - Requirements:
        - postId must be a number between 1 and 100.
        - postId should be in the "ENABLED" state.
3. Reprocess Post
    - Description: Reprocesses a post that is in the "ENABLED" or "DISABLED" state.
    - Method: PUT
    - Path: /posts/{postId}
    - Requirements:
        - postId must be a number between 1 and 100.
        - postId should be in the "ENABLED" or "DISABLED" state.
4. Query Posts
    - Description: Provides a list of posts.
    - Method: GET
    - Path: /posts
    - Response:
        - `title`: Can be null or empty depending on the state.
        - `body`: Can be null or empty depending on the state.
        - `comments`: Can be null or empty depending on the state.
        - `history`: Cannot be null or empty; it must always have a value.


![image2.png](https://github.com/ualaci/Blog-Challenge/blob/1eb073d539749f7ee0471b73336428bbd13246d2/Image2.png)



