package me.halin.me.halin.batch.service

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class BatchService(
    private val restTemplate: RestTemplate
) {
    fun <T> callApi(url: String, type: Class<T>): Any {

        // HttpHeaders 객체 생성
        val headers = HttpHeaders().also {
            it.set(HttpHeaders.CONTENT_TYPE, "application/avro")
        }

        // HttpEntity에 헤더 설정
        val entity = HttpEntity<Any>(headers)

        // GET 요청 보내기
        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            type
        )

        return response.body!!
    }
}