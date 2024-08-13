package me.halin.me.halin.server.repository

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Repository

@Repository
class ServerRepository {
    fun <T> search(query: String, index: String, type: Class<T>): T {
        /*
        * elasticsearch logic
        * */

        return ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(getJson(index), type)
    }

    private fun getJson(index: String): String {
        return when (index) {
            "user" -> {
                """
                    {
                        "_id": "11",
                        "name": "halin",
                        "age": 23,
                        "favoriteColor": "vertical service"
                    }
                """.trimIndent()
            }

            "store" -> {
                """
                    {
                        "_id": "11",
                    }
                """.trimIndent()
            }

            else -> {
                throw IllegalArgumentException("")
            }
        }
    }
}