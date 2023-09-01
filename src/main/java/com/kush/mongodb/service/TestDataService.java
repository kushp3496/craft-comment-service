package com.kush.mongodb.service;

import com.kush.mongodb.entity.Comment;
import com.kush.mongodb.entity.User;
import com.kush.mongodb.enums.CommentStatus;
import com.kush.mongodb.repository.CommentRepository;
import com.kush.mongodb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestDataService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    //    @EventListener(ApplicationReadyEvent.class)
    public void insertTestData() {
        User u1 = new User("u1");
        User u2 = new User("u2");
        User u3 = new User("u3");
        userRepository.save(u1).block();
        userRepository.save(u2).block();
        userRepository.save(u3).block();

        Comment c1 = new Comment("c1", u1.getId());
        commentRepository.save(c1).block();

        Comment c2 = new Comment("c2", u2.getId());
        commentRepository.save(c2).block();

        Comment c3 = new Comment("c3", u3.getId());
        commentRepository.save(c3).block();

        Comment c11 = new Comment("c11", u2.getId(), c1.getId());
        commentRepository.save(c11).block();

        Comment c31 = new Comment("c31", u1.getId(), c3.getId());
        commentRepository.save(c31).block();

        Comment c111 = new Comment("c111", u1.getId(), c11.getId());
        commentRepository.save(c111).block();

        Comment c112 = new Comment("c112", u2.getId(), c11.getId());
        commentRepository.save(c112).block();

        Comment c311 = new Comment("c311", u2.getId(), c31.getId());
        commentRepository.save(c311).block();

        Comment c3111 = new Comment("c3111", u3.getId(), c311.getId());
        commentRepository.save(c3111).block();

        c1.addLikeOrDislike(u1.getId(), CommentStatus.LIKE);
        c111.addLikeOrDislike(u1.getId(), CommentStatus.LIKE);
        c111.addLikeOrDislike(u2.getId(), CommentStatus.LIKE);
        c112.addLikeOrDislike(u3.getId(), CommentStatus.LIKE);
        c2.addLikeOrDislike(u3.getId(), CommentStatus.LIKE);
        c31.addLikeOrDislike(u3.getId(), CommentStatus.LIKE);
        c3111.addLikeOrDislike(u1.getId(), CommentStatus.LIKE);
        c3111.addLikeOrDislike(u2.getId(), CommentStatus.LIKE);

        c1.addLikeOrDislike(u3.getId(), CommentStatus.DISLIKE);
        c112.addLikeOrDislike(u1.getId(), CommentStatus.DISLIKE);
        c2.addLikeOrDislike(u2.getId(), CommentStatus.DISLIKE);
        c2.addLikeOrDislike(u1.getId(), CommentStatus.DISLIKE);
        c3.addLikeOrDislike(u2.getId(), CommentStatus.DISLIKE);
        c31.addLikeOrDislike(u2.getId(), CommentStatus.DISLIKE);

        commentRepository.save(c1).block();
        commentRepository.save(c111).block();
        commentRepository.save(c112).block();
        commentRepository.save(c2).block();
        commentRepository.save(c31).block();
        commentRepository.save(c3111).block();
        commentRepository.save(c3).block();
    }
}
