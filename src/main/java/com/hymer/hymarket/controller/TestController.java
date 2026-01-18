package com.hymer.hymarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testing")
public class TestController {
    @GetMapping("/hello")
    public String Greet(){
        return "Hello World";
    }


}
