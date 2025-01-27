package com.example.webflux1.controller;

import com.example.webflux1.dto.UserCreateRequest;
import com.example.webflux1.dto.UserResponse;
import com.example.webflux1.repository.User;
import com.example.webflux1.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
@AutoConfigureWebTestClient
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    void createUser() {
        // Given
        var user = new User(1L, "greg", "greg@fastcampus.co.kr", LocalDateTime.now(), LocalDateTime.now());

        // Mocking the service call
        when(userService.create("greg", "greg@fastcampus.co.kr"))
                .thenReturn(Mono.just(user));

        webTestClient.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateRequest("greg", "greg@fastcampus.co.kr"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UserResponse.class)
                .value(res -> {
                    assertEquals("greg", res.getName());
                    assertEquals("greg@fastcampus.co.kr", res.getEmail());
                });
    }
}