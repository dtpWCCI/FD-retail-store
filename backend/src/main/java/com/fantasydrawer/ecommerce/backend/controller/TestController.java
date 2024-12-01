package com.fantasydrawer.ecommerce.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public String testApi() {
        return "Test API is working!";
    }

    @GetMapping("/greet")
public String greet(@RequestParam(name = "name", defaultValue = "User") String name) {
    return "Hello, " + name + "!";
}

}
