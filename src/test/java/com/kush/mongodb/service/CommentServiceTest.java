package com.kush.mongodb.service;

import com.kush.mongodb.entity.Comment;
import com.kush.mongodb.entity.User;
import com.kush.mongodb.enums.CommentStatus;
import com.kush.mongodb.repository.CommentRepository;
import com.kush.mongodb.repository.UserRepository;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.kush.mongodb.util.TestUtils.convertObjectToString;
import static com.kush.mongodb.util.TestUtils.getJSONFromFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    User user1, user2;
    Comment comment1, comment2, comment3;

    @BeforeEach
    public void init() {
        user1 = new User();
        user1.setId("test_userId");
        user1.setName("test_user");
        user2 = new User();
        user2.setId("test_userId1");
        user2.setName("test_user1");
        comment1 = new Comment("hello", "test_userId");
        comment2 = new Comment("hello", "test_userId1");
        comment3 = new Comment("hello comment3", "test_userId1");
    }

    @Test
    void test_getComment() throws JSONException {
        when(commentRepository.findById(anyString())).thenReturn(Mono.just(comment1));
        when(userRepository.findById(anyString())).thenReturn(Mono.just(user1));

        var response = commentService.getComment("test_commentId");

        String expected = getJSONFromFile("src/test/resources/TestComment1.json");
        String actual = convertObjectToString(response.block());
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void test_getRootComments() throws JSONException {
        when(commentRepository.findRootComments(any())).thenReturn(Flux.just(comment1, comment2));
        when(userRepository.findById(anyString())).thenReturn(Mono.just(user1)).thenReturn(Mono.just(user2));

        var response = commentService.getRootComments(0, 2).collectList().block();

        String expected = getJSONFromFile("src/test/resources/TestComment2.json");
        String actual = convertObjectToString(response);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void test_getChildComments() throws JSONException {
        comment1.setId("test_parentCommentId");
        when(commentRepository.findById(anyString())).thenReturn(Mono.just(comment1));
        when(commentRepository.findByParentCommentId(anyString(), any())).thenReturn(Flux.just(comment2, comment3));
        when(userRepository.findById(anyString())).thenReturn(Mono.just(user2)).thenReturn(Mono.just(user2));

        var response = commentService.getChildComments("test", 0, 2).collectList().block();

        String expected = getJSONFromFile("src/test/resources/TestComment3.json");
        String actual = convertObjectToString(response);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void test_saveComment() throws JSONException {
        user1.setId("test_userId1");
        comment1.setUserId("test_userId1");
        when(userRepository.findById(anyString())).thenReturn(Mono.just(user1));
        when(commentRepository.save(any())).thenReturn(Mono.just(comment1));

        var response = commentService.saveComment(comment1).block();

        String expected = getJSONFromFile("src/test/resources/TestComment4.json");
        String actual = convertObjectToString(response);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void test_saveComment_invalidUserId() {
        when(userRepository.findById(anyString())).thenReturn(Mono.empty());
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> commentService.saveComment(comment1).block());
        Assertions.assertEquals("User does not exists!", thrown.getMessage());
    }

    @Test
    void test_reply() throws JSONException {
        comment2.setUserId("test_userId2");
        when(userRepository.findById(anyString())).thenReturn(Mono.just(user1));
        when(commentRepository.findById(anyString())).thenReturn(Mono.just(comment1));
        when(commentRepository.save(any())).thenReturn(Mono.just(comment2));

        var response = commentService.reply("test_parentCommentId", comment2).block();

        String expected = getJSONFromFile("src/test/resources/TestComment5.json");
        String actual = convertObjectToString(response);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void test_toggleLikeDislike_noLikeDislikePresent() {
        when(userRepository.findById(anyString())).thenReturn(Mono.just(user1));
        when(commentRepository.findById(anyString())).thenReturn(Mono.just(comment1));
        when(commentRepository.save(any())).thenAnswer(i -> Mono.just(i.getArguments()[0]));

        var response = commentService.toggleLikeDislike("test_userId", "test_commentId", CommentStatus.LIKE).block();
        Assertions.assertNotNull(response);

        var optional = response.getLikesDislikes().stream()
                .filter(likeDislike -> likeDislike.getUserId().equals(user1.getId()))
                .findFirst();
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals(CommentStatus.LIKE, optional.get().getCommentStatus());
    }

    @Test
    void test_toggleLikeDislike_likeDislikePresentForDifferentUser() {
        comment1.addLikeOrDislike("other_userId", CommentStatus.DISLIKE);
        when(userRepository.findById(anyString())).thenReturn(Mono.just(user1));
        when(commentRepository.findById(anyString())).thenReturn(Mono.just(comment1));
        when(commentRepository.save(any())).thenAnswer(i -> Mono.just(i.getArguments()[0]));

        var response = commentService.toggleLikeDislike("test_userId", "test_commentId", CommentStatus.DISLIKE).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.getLikesDislikes().size());
        var optional = response.getLikesDislikes().stream()
                .filter(likeDislike -> likeDislike.getUserId().equals(user1.getId()))
                .findFirst();
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals(CommentStatus.DISLIKE, optional.get().getCommentStatus());
    }

    @Test
    void test_toggleLikeDislike_likeDislikePresentForSameUser_withSameAction() {
        comment1.addLikeOrDislike("test_userId", CommentStatus.LIKE);
        when(userRepository.findById(anyString())).thenReturn(Mono.just(user1));
        when(commentRepository.findById(anyString())).thenReturn(Mono.just(comment1));
        when(commentRepository.save(any())).thenAnswer(i -> Mono.just(i.getArguments()[0]));

        var response = commentService.toggleLikeDislike("test_userId", "test_commentId", CommentStatus.LIKE).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(0, response.getLikesDislikes().size());
    }

    @Test
    void test_toggleLikeDislike_likeDislikePresentForSameUser_withDifferentAction() {
        comment1.addLikeOrDislike("test_userId", CommentStatus.DISLIKE);
        when(userRepository.findById(anyString())).thenReturn(Mono.just(user1));
        when(commentRepository.findById(anyString())).thenReturn(Mono.just(comment1));
        when(commentRepository.save(any())).thenAnswer(i -> Mono.just(i.getArguments()[0]));

        var response = commentService.toggleLikeDislike("test_userId", "test_commentId", CommentStatus.LIKE).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getLikesDislikes().size());
        var optional = response.getLikesDislikes().stream()
                .filter(likeDislike -> likeDislike.getUserId().equals(user1.getId()))
                .findFirst();
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals(CommentStatus.LIKE, optional.get().getCommentStatus());
    }
}
