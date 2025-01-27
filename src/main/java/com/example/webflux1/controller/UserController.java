package com.example.webflux1.controller;

import com.example.webflux1.dto.UserCreateRequest;
import com.example.webflux1.dto.UserPostResponse;
import com.example.webflux1.dto.UserResponse;
import com.example.webflux1.dto.UserUpdateRequest;
import com.example.webflux1.service.PostServieV2;
import com.example.webflux1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final PostServieV2 postServieV2;

    @PostMapping("")
    public Mono<UserResponse> createUser(@RequestBody UserCreateRequest request){
       return userService.create(request.getName(), request.getEmail())
               .map(UserResponse::of);
    }

    @GetMapping("")
    public Flux<UserResponse> findAllUsers(){
        return userService.findAll()
                .map(UserResponse::of);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> findUser(@PathVariable Long id){
        return userService.findById(id)
                // 유저 존재시 -> 200 OK
                .map(u -> ResponseEntity.ok(UserResponse.of(u)))
                // 유저 미존재시 -> 404 not Found
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<?>> deleteUser(@PathVariable Long id){
        // 204(no content) - 일반적으로 Mono<Dto> return 시 HttpStatus는 200
        return userService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping("/search")
    public Mono<ResponseEntity<?>> deleteUser(@RequestParam String name){
        // 204(no content) - 일반적으로 Mono<Dto> return 시 HttpStatus는 200
        return userService.deleteByName(name)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request){
        return userService.update(id, request.getName(), request.getEmail())
                // 업데이트된 유저 존재시 -> 200 OK
                .map(u -> ResponseEntity.ok(UserResponse.of(u)))
                // 업데이트된 유저 미존재시 -> 404 not Found
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

    }

    @GetMapping("/{id}/posts")
    public Flux<UserPostResponse> getUserPosts(@PathVariable Long id){
        return postServieV2.findAllByUserId(id)
                .map(UserPostResponse::of);
    }
}
