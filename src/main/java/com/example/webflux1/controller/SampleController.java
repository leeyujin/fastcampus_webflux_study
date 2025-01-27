package com.example.webflux1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SampleController {

    @GetMapping("sample/hello")
    public Mono<String> getHello() {

        // reactor
        // publisher <---> subscriber
        // 리엑터는 구독하지않으면 동작하지않음
        // webflux는 publisher만 제공해도됨

        return Mono.just("hello rest controller with webflux");
    }
}
