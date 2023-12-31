package com.kush.craft.demo.controller;

import com.kush.craft.demo.entity.Comment;
import com.kush.craft.demo.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CommentControllerTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private WebTestClient webClient;

    @Test
    void testGetRootComments() {
        when(commentService.getRootComments(anyInt(), anyInt())).thenReturn(Flux.just(new Comment()));

        webClient.get()
                .uri("/fetch/0/2")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Comment.class);
    }
}
