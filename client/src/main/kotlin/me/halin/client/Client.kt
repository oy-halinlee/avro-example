package me.halin.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Client

fun main(args: Array<String>) {
    runApplication<Client>(*args)
}
