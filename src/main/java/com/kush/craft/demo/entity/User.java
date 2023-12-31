package com.kush.craft.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document
@NoArgsConstructor
public class User {

    @Id
    private String id;
    private String name;

    @JsonIgnore
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonIgnore
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public User(String name) {
        this.name = name;
    }

}
