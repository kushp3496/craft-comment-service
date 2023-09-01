package com.kush.craft.demo.request;

import com.kush.craft.demo.enums.CommentStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ToggleCommentStatusRequest {
    private final String userId;
    private final CommentStatus commentStatus;
}
