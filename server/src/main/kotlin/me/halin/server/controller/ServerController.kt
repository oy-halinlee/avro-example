package me.halin.me.halin.server.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.halin.me.halin.server.dto.request.SearchSqlRequest
import me.halin.me.halin.server.service.ServerService
import me.halin.me.halin.server.service.ServerSqlService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ServerController(
    private val serverService: ServerService,
    private val serverSqlService: ServerSqlService
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

    @GetMapping("/search/sql")
    fun searchBySqlQuery(
        @RequestBody searchSqlRequest: SearchSqlRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(
            serverSqlService.search<Any>(searchSqlRequest)
        )
    }
}