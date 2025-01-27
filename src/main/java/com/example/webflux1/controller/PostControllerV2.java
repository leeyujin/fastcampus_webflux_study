package com.example.webflux1.controller;

import com.example.webflux1.dto.PostCreateRequest;
import com.example.webflux1.dto.PostResponse;
import com.example.webflux1.dto.PostResponseV2;
import com.example.webflux1.service.PostServieV2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/posts")
public class PostControllerV2 {
    private final PostServieV2 postServieV2;

    @PostMapping("")
    public Mono<PostResponseV2> create(@RequestBody PostCreateRequest request) {
        return postServieV2.create(request.getUserId(), request.getTitle(), request.getContent())
                .map(PostResponseV2::of);
    }

    @GetMapping("")
    public Flux<PostResponseV2> findAllPost() {
        return postServieV2.findAll().map(PostResponseV2::of);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PostResponseV2>> findAllPost(@PathVariable Long id) {
        return postServieV2.findById(id)
                .map(post -> ResponseEntity.ok(PostResponseV2.of(post)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<PostResponseV2>> deletePost(@PathVariable Long id) {
        return postServieV2.deleteById(id)
                .then(Mono.just(ResponseEntity.notFound().build()));
    }
}
