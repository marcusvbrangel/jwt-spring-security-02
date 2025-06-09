package com.mvbr.jwtspringsecurity02.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/example")
public class ExampleController {
    @GetMapping
    public String hello() {
        return "API funcionando!";
    }
}

