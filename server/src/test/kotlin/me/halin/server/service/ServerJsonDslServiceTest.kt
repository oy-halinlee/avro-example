package me.halin.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.mockk.mockk
import io.mockk.spyk
import me.halin.me.halin.server.dto.request.SearchJsonDslRequest
import me.halin.me.halin.server.repository.ServerRepository
import me.halin.me.halin.server.service.ServerJsonDslService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ServerJsonDslServiceTest : ESQueryTest {
    private lateinit var serverJsonDslService: ServerJsonDslService
    private lateinit var serverRepository: ServerRepository

    @BeforeEach
    fun init() {
        serverRepository = mockk()
        serverJsonDslService = spyk(
            ServerJsonDslService(serverRepository),
            recordPrivateCalls = true
        )
    }

    @Test
    override fun `기본 조건 term query`() {
        // a and b
        val jsonString = """
            {
              "condition": {
                "and": [
                  {
                    "field": "a",
                    "operation": "equals",
                    "value": "a"
                  },
                  {
                    "field": "b",
                    "operation": "equals",
                    "value": "b"
                  }
                ]
              }
            }
        """.trimIndent()

        print(jsonString)
    }

    @Test
    override fun `기본 조건 match query`() {
        val jsonString = """
            {
              "condition": {
                "and": [
                  {
                    "field": "a",
                    "operation": "contains",
                    "value": "a"
                  }
                ]
              }
            }
        """.trimIndent()

        print(jsonString)
    }

    @Test
    override fun `중첩된 조건문 쿼리를 생성`() {
        //  (a and b ) or (c and d)
        val jsonString = """
                    {
                      "size": 10,
                      "page": 2,
                      "condition": {
                        "or": [
                          {
                            "and": [
                              {
                                "field": "A",
                                "operation": "equals",
                                "value": "valueA"
                              },
                              {
                                "field": "B",
                                "operation": "equals",
                                "value": "valueB"
                              }
                            ]
                          },
                          {
                            "and": [
                              {
                                "field": "C",
                                "operation": "equals",
                                "value": "valueC"
                              },
                              {
                                "field": "D",
                                "operation": "equals",
                                "value": "valueD"
                              }
                            ]
                          }
                        ]
                      }
                    }
        """.trimIndent()

        print(jsonString)
    }

    @Test
    override fun `range 쿼리를 생성`() {
        // range a >
        val jsonString = """
                    {
                      "size": 10,
                      "page": 2,
                      "condition": {
                        "and": [
                              {
                                "field": "A",
                                "operation": "greater_than",
                                "value": "2024-10-11"
                              },
                              {
                                "field": "B",
                                "operation": "less_than",
                                "value": "2024-10-12"
                              }
                            ]
                      }
                    }
        """.trimIndent()

        print(jsonString)
    }

    @Test
    override fun `query_string 쿼리를 생성`() {
        // range a >
        val jsonString = """
                    {
                      "size": 10,
                      "page": 2,
                      "condition": {
                        "and": [
                              {
                                "field": "A",
                                "operation": "like",
                                "value": "a*"
                              }
                            ]
                      }
                    }
        """.trimIndent()

        print(jsonString)
    }

    @Test
    override fun `terms 쿼리를 생성`() {
        // range a >
        val jsonString = """
                    {
                      "size": 10,
                      "page": 2,
                      "condition": {
                        "and": [
                              {
                                "field": "A",
                                "operation": "terms",
                                "value": ["a", "b", "c", "d", "e", "f"]
                              }
                            ]
                      }
                    }
        """.trimIndent()

        print(jsonString)
    }


    private fun print(jsonString: String) {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        val queryJson = mapper.readValue(jsonString, SearchJsonDslRequest::class.java)

        val esQuery = serverJsonDslService.convertToESQuery(queryJson)
        val esQueryJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(esQuery)

        println(esQueryJson)
    }
}