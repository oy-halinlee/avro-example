package me.halin.client.controller

import co.kr.oliveyoung.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
class ClientController {
    @GetMapping("/user", produces = ["application/avro"])
    fun user(): User = User(
        "halin",
        Random.nextInt(),
        "vertical service"
    )
}
