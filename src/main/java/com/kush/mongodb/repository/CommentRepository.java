package com.kush.mongodb.repository;

import com.kush.mongodb.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {

    // fetch comment with given parent comment id, paginated & sorted by id
    public Flux<Comment> findByParentCommentId(String parentCommentId, Pageable pageable);

    @Query(value = "{ parentCommentId : { $exists : false } }")
    public Flux<Comment> findRootComments(Pageable pageable);
}