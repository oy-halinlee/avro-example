package me.halin.me.halin.server.service

import me.halin.me.halin.server.repository.ServerRepository
import org.springframework.stereotype.Service


@Service
class ServerService(
    private val serverRepository: ServerRepository
) {
    fun <T> search(query: String, index: String, type: Class<T>): T {
        /*
        * service logic
        * */
        return serverRepository.search(query, index, type)
    }
}