package com.kush.mongodb.controller;

import com.kush.mongodb.entity.Comment;
import com.kush.mongodb.entity.LikeDislike;
import com.kush.mongodb.request.ToggleCommentStatusRequest;
import com.kush.mongodb.service.CommentService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/fetch/{pageNo}/{limit}")
    public Flux<Comment> getRootComments(@PathVariable int pageNo, @PathVariable int limit) {
        return commentService.getRootComments(pageNo, limit);
    }

    @GetMapping("/fetch-child-comments/{id}/{pageNo}/{limit}")
    public Flux<Comment> getChildComments(@PathVariable String id, @PathVariable int pageNo, @PathVariable int limit) {
        return commentService.getChildComments(id, pageNo, limit);
    }

    @GetMapping("/fetch/{id}")
    public Mono<Comment> getComment(@PathVariable String id) {
        return commentService.getComment(id);
    }

    @PostMapping("/comment")
    public Mono<Comment> comment(@RequestBody Comment comment) {
        return commentService.saveComment(comment);
    }

    @PostMapping("/reply/{parentCommentId}")
    public Mono<Comment> reply(@PathVariable String parentCommentId, @RequestBody Comment comment) {
        return commentService.reply(parentCommentId, comment);
    }

    @PatchMapping("/toggle-comment-status/{commentId}")
    public Mono<Comment> toggleCommentStatus(@PathVariable String commentId, @RequestBody ToggleCommentStatusRequest request) {
        return commentService.toggleLikeDislike(request.getUserId(), commentId, request.getCommentStatus());
    }

    @GetMapping("/likes-dislikes/{commentId}/{pageNo}/{limit}")
    public Flux<LikeDislike> getLikeDislikeForComment(@PathVariable String commentId, @PathVariable int pageNo, @PathVariable int limit) {
        return commentService.fetchLikesDislikes(commentId, pageNo, limit);
    }
}
