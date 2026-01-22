package com.bandverse.app.data.local

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object SupabaseClient {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(json)
        }
        defaultRequest {
            url("${SupabaseConfig.SUPABASE_URL}/rest/v1")
            headers.append("apikey", SupabaseConfig.SUPABASE_ANON_KEY)
            headers.append("Authorization", "Bearer ${SupabaseConfig.SUPABASE_ANON_KEY}")
            contentType(ContentType.Application.Json)
        }
    }

    init {
        println("=== Supabase Client Initialized ===")
        println("URL: ${SupabaseConfig.SUPABASE_URL}")
        println("Anon Key: ${SupabaseConfig.SUPABASE_ANON_KEY.take(10)}...")
    }

    suspend inline fun <reified T> get(
        table: String,
        filters: Map<String, String> = emptyMap(),
        limit: Int = 50,
        offset: Int = 0,
        select: String = "*"
    ): List<T> {
        var queryString = "?select=$select&limit=$limit&offset=$offset"
        filters.forEach { (key, value) ->
            queryString += "&$key=$value"
        }
        val fullUrl = "$table$queryString"
        println("=== Supabase GET Request ===")
        println("URL: $fullUrl")
        
        return try {
            val response = httpClient.get(fullUrl)
            println("Response status: ${response.status}")
            println("Response successful: ${response.status.value in 200..299}")
            response.body()
        } catch (e: Exception) {
            println("=== Supabase GET Error ===")
            println("Error: ${e.message}")
            println("Stack trace: ${e.stackTraceToString()}")
            emptyList()
        }
    }

    suspend inline fun <reified T> post(
        table: String,
        data: Any,
        select: String = "*"
    ): List<T> {
        return try {
            println("=== Supabase POST Request ===")
            println("URL: $table?select=$select")
            val response = httpClient.post("$table?select=$select") {
                setBody(data)
            }
            println("Response status: ${response.status}")
            response.body()
        } catch (e: Exception) {
            println("=== Supabase POST Error ===")
            println("Error: ${e.message}")
            emptyList()
        }
    }

    // Test connection function
    suspend fun testConnection(): Boolean {
        return try {
            println("=== Testing Supabase Connection ===")
            println("Attempting to fetch bands table...")
            
            // 使用公共的 get 方法测试连接
            val response = get<Map<String, Any>>(
                table = "bands",
                limit = 1
            )
            
            val success = response.isNotEmpty()
            
            if (success) {
                println("✓ Connection successful!")
                println("✓ Found ${response.size} record(s)")
                true
            } else {
                println("✗ Connection failed - No data found")
                false
            }
        } catch (e: Exception) {
            println("✗ Connection test failed: ${e.message}")
            println("Stack trace: ${e.stackTraceToString()}")
            
            // 根据错误信息判断可能的原因
            when {
                e.message?.contains("401") == true -> {
                    println("✗ 可能原因：认证失败，请检查 API 密钥")
                }
                e.message?.contains("404") == true -> {
                    println("✗ 可能原因：表不存在，请检查数据库架构")
                }
                e.message?.contains("Network") == true -> {
                    println("✗ 可能原因：网络连接问题")
                }
                else -> {
                    println("✗ 未知错误，请检查详细信息")
                }
            }
            false
        }
    }

    suspend inline fun <reified T> patch(
        table: String,
        data: Any,
        filters: Map<String, String> = emptyMap(),
        select: String = "*"
    ): List<T> {
        var queryString = "?select=$select"
        filters.forEach { (key, value) ->
            queryString += "&$key=$value"
        }
        return httpClient.patch("$table$queryString") {
            setBody(data)
        }.body()
    }
}
