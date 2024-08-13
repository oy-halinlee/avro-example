package me.halin.me.halin.server.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.halin.me.halin.server.service.ServerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ServerController(
    private val serverService: ServerService,
) {
    @GetMapping("/search")
    fun search(
        @RequestParam type: String,
        @RequestParam query: String,
        @RequestParam indexName: String,
    ): Map<*, *> {
        val schema = Class.forName("co.kr.oliveyoung.$type")
            .getDeclaredConstructor()
            .newInstance()

        return ObjectMapper()
            .readValue(
                serverService.search(query, indexName, schema::class.java).toString(),
                Map::class.java
            )
    }
}