package com.kush.craft.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kush.craft.demo.enums.CommentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Document
@NoArgsConstructor
public class Comment {

    @Id
    private String id;
    private String text;
    private String userId;

    @Transient
    private String userName;

    @JsonIgnore
    private String parentCommentId;

    private Set<LikeDislike> likesDislikes;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Comment(String text, String userId, String parentCommentId) {
        this.text = text;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
    }

    public Comment(String text, String userId) {
        this.text = text;
        this.userId = userId;
    }

    public void addLikeOrDislike(String userId, CommentStatus commentStatus) {
        if (Objects.isNull(likesDislikes))
            likesDislikes = new HashSet<>();
        likesDislikes.add(new LikeDislike(userId, commentStatus, LocalDateTime.now(), LocalDateTime.now()));
    }

}
