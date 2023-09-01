package com.kush.craft.demo.entity;

import com.kush.craft.demo.enums.CommentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LikeDislike {

    private String userId;
    CommentStatus commentStatus;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public LikeDislike() {

    }

    public LikeDislike(String userId, CommentStatus commentStatus) {
        this.userId = userId;
        this.commentStatus = commentStatus;
    }

    public void toggle() {
        this.commentStatus = this.commentStatus == CommentStatus.LIKE ? CommentStatus.DISLIKE : CommentStatus.LIKE;
    }
}
