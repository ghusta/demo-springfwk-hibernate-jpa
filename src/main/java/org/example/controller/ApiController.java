package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/api")
public class ApiController {

    @GetMapping(path = "version")
    public Map<String, String> version() {
        return Map.of("version", "1.2.3");
    }

    @GetMapping(path = "status")
    public Map<String, String> status() {
        return Map.of("status", "UP");
    }

}
