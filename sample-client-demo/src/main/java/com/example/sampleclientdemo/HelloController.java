package com.example.sampleclientdemo;

import com.example.sampleapidemo.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class HelloController {

    @Resource
    HelloService helloService;

    @GetMapping("/hello")
    public String hello() {
        return helloService.hello("World");
    }
}
