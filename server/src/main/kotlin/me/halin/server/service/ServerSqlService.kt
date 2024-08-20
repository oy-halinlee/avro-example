package me.halin.me.halin.server.service

import me.halin.me.halin.server.dto.request.SearchSqlRequest
import me.halin.me.halin.server.repository.ServerRepository
import net.sf.jsqlparser.expression.*
import net.sf.jsqlparser.expression.Function
import net.sf.jsqlparser.expression.operators.conditional.AndExpression
import net.sf.jsqlparser.expression.operators.conditional.OrExpression
import net.sf.jsqlparser.expression.operators.relational.*
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import org.springframework.stereotype.Service

@Service
class ServerSqlService(
    private val serverRepository: ServerRepository
) {
    fun <T> search(searchSqlRequest: SearchSqlRequest) {
        sqlToElasticsearch(searchSqlRequest.sqlQuery)
    }

    fun sqlToElasticsearch(sql: String): Map<String, Any> {
        val statement: Statement = CCJSqlParserUtil.parse(sql)
        val selectBody = (statement as Select).selectBody as PlainSelect

        val esQuery = mutableMapOf<String, Any>()

        // WHERE 절 처리
        val where = selectBody.where
        where?.let {
            esQuery["query"] = parseExpression(it)
        }

        // ORDER BY 절 처리
        selectBody.orderByElements?.let { orderByElements ->
            val sort = orderByElements.map { orderByElement ->
                val column = orderByElement.expression.toString()
                val order = if (orderByElement.isAsc) "asc" else "desc"
                mapOf(column to mapOf("order" to order))
            }
            esQuery["sort"] = sort
        }

        // LIMIT 절 처리
        val limit = selectBody.limit
        limit?.let {
            esQuery["size"] = it.rowCount.toString().toLong()
            if (it.offset != null) {
                esQuery["from"] = it.offset.toString().toLong()
            }
        }

        return esQuery
    }

    fun parseExpression(expr: Expression): Map<String, Any> {
        return when (expr) {
            is EqualsTo -> {
                val column = (expr.leftExpression as Column).columnName
                val value = expr.rightExpression.toElasticsearchValue()
                mapOf("term" to mapOf(column to value))
            }

            is GreaterThan -> {
                val column = (expr.leftExpression as Column).columnName
                val value = expr.rightExpression.toElasticsearchValue()
                mapOf("range" to mapOf(column to mapOf("gt" to value)))
            }

            is GreaterThanEquals -> {
                val column = (expr.leftExpression as Column).columnName
                val value = expr.rightExpression.toElasticsearchValue()
                mapOf("range" to mapOf(column to mapOf("gte" to value)))
            }

            is MinorThan -> {
                val column = (expr.leftExpression as Column).columnName
                val value = expr.rightExpression.toElasticsearchValue()
                mapOf("range" to mapOf(column to mapOf("lt" to value)))
            }

            is MinorThanEquals -> {
                val column = (expr.leftExpression as Column).columnName
                val value = expr.rightExpression.toElasticsearchValue()
                mapOf("range" to mapOf(column to mapOf("lte" to value)))
            }

            is LikeExpression -> {
                val column = (expr.leftExpression as Column).columnName
                val value = (expr.rightExpression as StringValue).value
                val queryStringValue = value.replace("%", "*")

                mapOf(
                    "query_string" to mapOf(
                        "default_field" to "$column",
                        "query" to "$queryStringValue"
                    )
                )
            }

            is Between -> {
                val column = (expr.leftExpression as Column).columnName
                val startValue = expr.betweenExpressionStart.toElasticsearchValue()
                val endValue = expr.betweenExpressionEnd.toElasticsearchValue()
                mapOf(
                    "range" to mapOf(
                        column to mapOf(
                            "gte" to startValue,
                            "lte" to endValue
                        )
                    )
                )
            }

            is InExpression -> {
                val column = (expr.leftExpression as Column).columnName
                val itemsList = expr.rightExpression as ExpressionList<*>
                val values = itemsList.expressions.map { (it as StringValue).value }
                mapOf("terms" to mapOf(column to values))
            }

            is AndExpression -> {
                mapOf(
                    "bool" to mapOf(
                        "must" to listOf(parseExpression(expr.leftExpression), parseExpression(expr.rightExpression))
                    )
                )
            }

            is OrExpression -> {
                mapOf(
                    "bool" to mapOf(
                        "should" to listOf(parseExpression(expr.leftExpression), parseExpression(expr.rightExpression))
                    )
                )
            }

            is Function -> {
                // 매칭 함수 처리
                val functionName = expr.name.lowercase()
                if (functionName == "match") {
                    val params = expr.parameters as ExpressionList

                    if (params.size == 2) {
                        val field = (params[0] as StringValue).value
                        val value = params[1].toElasticsearchValue()

                        mapOf("match" to mapOf(field to value))
                    } else {
                        throw IllegalArgumentException("match function requires exactly 2 parameters")
                    }
                } else {
                    throw IllegalArgumentException("Unsupported function: $functionName")
                }
            }

            is Parenthesis -> {
                parseExpression(expr.expression)
            }

            else -> throw IllegalArgumentException("Unsupported expression type: ${expr::class.simpleName}")
        }
    }

    fun Expression.toElasticsearchValue(): Any {
        return when (this) {
            is LongValue -> this.value
            is DoubleValue -> this.value
            is StringValue -> this.value
            is DateValue -> this.value.toString()
            is TimeValue -> this.value.toString()
            is TimestampValue -> this.value.toString()
            else -> throw IllegalArgumentException("Unsupported value type: ${this::class.simpleName}")
        }
    }
}