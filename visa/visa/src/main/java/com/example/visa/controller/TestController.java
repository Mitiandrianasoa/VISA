package com.example.visa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String test() {
        return "Application VISA fonctionne sur le port 8000 !";
    }


}