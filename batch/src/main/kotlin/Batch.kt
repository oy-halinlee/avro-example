package me.halin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Batch

fun main(args: Array<String>) {
    runApplication<Batch>(*args)
}