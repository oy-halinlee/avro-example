package me.halin.me.halin.batch.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.halin.me.halin.batch.service.BatchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BatchController(
    private val batchService: BatchService,
) {
    @GetMapping("/batch")
    fun trigger(
        @RequestParam type: String,
        @RequestParam indexName: String,
        @RequestParam url: String
    ): Map<*, *> {
        val schema = Class.forName("co.kr.oliveyoung.$type")
            .getDeclaredConstructor()
            .newInstance()

        val result = batchService.callApi(url, schema::class.java)

        return ObjectMapper().readValue(result.toString(), Map::class.java)
    }
}