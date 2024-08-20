package me.halin.me.halin.server.service

import me.halin.me.halin.server.dto.request.SearchJsonDslRequest
import me.halin.me.halin.server.repository.ServerRepository
import org.springframework.stereotype.Service

@Service
class ServerJsonDslService(
    private val serverRepository: ServerRepository
) {

    fun convertConditionToESQuery(condition: SearchJsonDslRequest.ConditionJson): Map<String, Any> {
        return when {
            condition.and != null -> mapOf(
                "bool" to mapOf(
                    "must" to condition.and.map { convertConditionToESQuery(it) }
                )
            )

            condition.or != null -> mapOf(
                "bool" to mapOf(
                    "should" to condition.or.map { convertConditionToESQuery(it) }
                )
            )

            condition.field != null && condition.operation != null -> {
                when (condition.operation) {
                    "equals" -> mapOf(
                        "term" to mapOf(
                            condition.field to condition.value
                        )
                    )

                    "greater_than" -> mapOf(
                        "range" to mapOf(
                            condition.field to mapOf("gt" to condition.value)
                        )
                    )

                    "less_than" -> mapOf(
                        "range" to mapOf(
                            condition.field to mapOf("lt" to condition.value)
                        )
                    )

                    // match
                    "contains" -> mapOf(
                        "match" to mapOf(
                            condition.field to condition.value
                        )
                    )

                    "terms" -> mapOf(
                        "terms" to mapOf(
                            condition.field to condition.value
                        )
                    )

                    "like" -> mapOf(
                        "simple_query_string" to mapOf(
                            "query" to condition.value,
                            "fields" to condition.field,
                            "default_operator" to "and"
                        )
                    )

                    "terms" -> mapOf(
                        "terms" to mapOf(
                            condition.field to listOf(condition.value),
                        )
                    )

                    else -> throw IllegalArgumentException("Unsupported operation: ${condition.operation}")
                }
            }

            else -> throw IllegalArgumentException("Invalid JSON structure")
        }
    }

    fun convertToESQuery(searchJsonDslRequest: SearchJsonDslRequest): Map<String, Any> {
        val from = (searchJsonDslRequest.page - 1) * searchJsonDslRequest.size
        val conditionQuery = convertConditionToESQuery(searchJsonDslRequest.condition)

        return mapOf(
            "size" to searchJsonDslRequest.size,
            "from" to from,
            "query" to conditionQuery
        )
    }
}