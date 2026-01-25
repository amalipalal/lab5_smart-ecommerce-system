package com.example.ecommerce_system;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class TestController {

    private record Test(UUID id, String name){}

    @QueryMapping
    public Test getTest(@Argument UUID id){
        return new Test(UUID.randomUUID(), "Hello world");
    }
}
