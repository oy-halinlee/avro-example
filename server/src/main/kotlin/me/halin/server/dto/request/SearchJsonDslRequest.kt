package me.halin.me.halin.server.dto.request

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SearchJsonDslRequest(
    val size: Int,
    val page: Int,
    val condition: ConditionJson
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class ConditionJson(
        val and: List<ConditionJson>? = null,
        val or: List<ConditionJson>? = null,
        val field: String? = null,
        val operation: String? = null,
        val value: Any? = null // Any를 사용하여 다양한 타입 지원
    )
}
