package org.example.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/api"])
class ApiController {
    @GetMapping(path = ["version"])
    fun version(): Map<String, String> {
        return mapOf("version" to "1.2.3")
    }

    @GetMapping(path = ["status"])
    fun status(): Map<String, String> {
        return mapOf("status" to "UP")
    }
}
