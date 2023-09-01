package com.kush.craft.demo.service;

import com.kush.craft.demo.entity.User;
import com.kush.craft.demo.entity.Comment;
import com.kush.craft.demo.entity.LikeDislike;
import com.kush.craft.demo.enums.CommentStatus;
import com.kush.craft.demo.repository.CommentRepository;
import com.kush.craft.demo.repository.UserRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentService(UserRepository userRepository,
                          CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public Mono<Comment> getComment(String id) {
        return commentRepository.findById(id)
                .flatMap(comment -> userRepository.findById(comment.getUserId())
                        .map(user -> {
                            comment.setUserName(user.getName());
                            return comment;
                        }));
    }

    public Flux<Comment> getRootComments(int pageNo, int limit) {
        return commentRepository.findRootComments(PageRequest.of(pageNo, limit, Sort.by("createdAt").descending()))
                .flatMap(comment -> userRepository.findById(comment.getUserId())
                        .map(user -> {
                            comment.setUserName(user.getName());
                            return comment;
                        }));
    }

    public Flux<Comment> getChildComments(String id, int pageNo, int limit) {
        return commentRepository.findById(id)
                .flatMapMany(parentComment ->
                        commentRepository.findByParentCommentId(parentComment.getId(), PageRequest.of(pageNo, limit, Sort.by("createdAt").descending())))
                .flatMap(comment -> userRepository.findById(comment.getUserId())
                        .map(user -> {
                            comment.setUserName(user.getName());
                            return comment;
                        }));
    }

    public Mono<Comment> saveComment(Comment comment) {
        return userRepository.findById(comment.getUserId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User does not exists!")))
                .flatMap(user -> {
                    comment.setUserId(user.getId());
                    return commentRepository.save(comment);
                });
    }

    public Mono<Comment> reply(String parentCommentId, Comment comment) {
        var user1 = userRepository.findById(comment.getUserId());
        var comment1 = commentRepository.findById(parentCommentId);
        return user1.zipWith(comment1)
                .flatMap(tuple -> {
                    User user2 = tuple.getT1();
                    Comment comment2 = tuple.getT2();
                    Comment reply = new Comment(comment.getText(), user2.getId(), comment2.getId());
                    return commentRepository.save(reply);
                });
    }

    public Mono<Comment> toggleLikeDislike(String userId, String commentId, CommentStatus commentStatus) {
        return userRepository.findById(userId)
                .zipWith(commentRepository.findById(commentId))
                .flatMap(tuple -> {
                    User user = tuple.getT1();
                    Comment comment = tuple.getT2();
                    if (CollectionUtils.isEmpty(comment.getLikesDislikes())) {
                        comment.addLikeOrDislike(user.getId(), commentStatus);
                    } else {
                        Optional<LikeDislike> likeDislikeOptional = comment.getLikesDislikes().stream()
                                .filter(likeDislike -> likeDislike.getUserId().equals(userId))
                                .findFirst();
                        if (likeDislikeOptional.isPresent()) {
                            LikeDislike likeDislike = likeDislikeOptional.get();
                            likeDislike.setUpdatedAt(LocalDateTime.now());
                            if (likeDislike.getCommentStatus().equals(commentStatus)) {
                                comment.getLikesDislikes().remove(likeDislike);
                            } else {
                                likeDislike.toggle();
                                comment.getLikesDislikes().add(likeDislike);
                            }
                        } else {
                            comment.addLikeOrDislike(user.getId(), commentStatus);
                        }
                    }
                    return commentRepository.save(comment);
                });
    }

    public Flux<LikeDislike> fetchLikesDislikes(String commentId, int pageNo, int limit) {
        return commentRepository.findById(commentId)
                .map(Comment::getLikesDislikes)
                .flatMapMany(Flux::fromIterable)
                .skip((long) (pageNo) * limit)
                .take(limit);
    }
}
