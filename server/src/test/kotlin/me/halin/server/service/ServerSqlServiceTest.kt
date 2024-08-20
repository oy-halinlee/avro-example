package me.halin.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.mockk
import io.mockk.spyk
import me.halin.me.halin.server.repository.ServerRepository
import me.halin.me.halin.server.service.ServerSqlService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class ServerSqlServiceTest : ESQueryTest {
    private lateinit var serverSqlService: ServerSqlService
    private lateinit var serverRepository: ServerRepository

    @BeforeEach
    fun init() {
        serverRepository = mockk()
        serverSqlService = spyk(
            ServerSqlService(serverRepository),
            recordPrivateCalls = true
        )
    }

    @Test
    override fun `기본 조건 term query`() {
        val sqlQuery = "select * from index_name where a = 'a' and b = 'b' order by _id ASC limit 100"

        print(sqlQuery)
    }

    @Test
    override fun `기본 조건 match query`() {
        val sqlQuery = "select * from index_name where match('a', 'a') order by _id ASC limit 100"

        print(sqlQuery)
    }

    @Test
    override fun `중첩된 조건문 쿼리를 생성`() {
        val sqlQuery =
            "select * from index_name where (a = 'a' or b = 'b') and (c = 'c' or d = 'd') order by _id ASC limit 100"

        print(sqlQuery)
    }

    @Test
    override fun `range 쿼리를 생성`() {
        val sqlQuery =
            "select * from index_name where A >= '2024-10-11' and B < '2024-10-12' order by _id ASC limit 100"

        print(sqlQuery)
    }

    @Test
    fun `range 한번의 2가지 조건으로 실행`() {
        val sqlQuery =
            "select * from index_name where A between '2024-10-11' and '2024-10-12' order by _id ASC limit 100"

        print(sqlQuery)
    }

    @Test
    override fun `query_string 쿼리를 생성`() {
        val sqlQuery =
            "select * from index_name where A like '%a'"

        print(sqlQuery)
    }

    @Test
    override fun `terms 쿼리를 생성`() {
        val sqlQuery =
            "select * from index_name where a in ('a', 'b', 'c', 'd', 'e', 'f')"

        print(sqlQuery)
    }

    private fun print(sql: String) {
        val esQuery = serverSqlService.sqlToElasticsearch(sql)

        // JSON 출력
        val jsonOutput = ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(esQuery)
        println(jsonOutput)
    }
}