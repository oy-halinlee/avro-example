package me.halin.server.service

interface ESQueryTest {
    fun `기본 조건 term query`()

    fun `기본 조건 match query`()

    fun `중첩된 조건문 쿼리를 생성`()

    fun `range 쿼리를 생성`()

    fun `query_string 쿼리를 생성`()

    fun `terms 쿼리를 생성`()
}