package com.bandverse.app.data.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 简单的连接测试工具
 * 可以在应用启动时或任何时候调用此函数来测试 Supabase 连接
 */
object ConnectionTest {
    
    /**
     * 执行完整的连接测试
     * 会测试基本的连接、认证、数据访问等
     */
    fun runFullTest(onComplete: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = SupabaseClient.testConnection()
            val message = if (result) {
                "✓ 连接成功！Supabase 数据库可正常访问"
            } else {
                "✗ 连接失败！请检查网络、API 配置和数据库设置"
            }
            onComplete(result, message)
        }
    }
    
    /**
     * 测试特定表的访问
     */
    suspend fun testTableAccess(tableName: String): Boolean {
        return try {
            println("=== Testing table access: $tableName ===")
            
            // 使用 SupabaseClient 的 get 方法测试连接
            val response = SupabaseClient.get<Map<String, Any>>(
                table = tableName,
                limit = 1
            )
            
            val success = response.isNotEmpty()
            println("Table $tableName access: ${if (success) "✓ Success (found ${response.size} records)" else "✗ No data found"}")
            
            success
        } catch (e: Exception) {
            println("Table $tableName access failed: ${e.message}")
            println("Stack trace: ${e.stackTraceToString()}")
            false
        }
    }
    
    /**
     * 测试所有主要表
     */
    suspend fun testAllTables(): Map<String, Boolean> {
        val tables = listOf(
            "profiles",
            "bands",
            "albums", 
            "songs",
            "user_recommendations",
            "daily_recommendations"
        )
        
        val results = mutableMapOf<String, Boolean>()
        
        for (table in tables) {
            results[table] = testTableAccess(table)
        }
        
        println("=== All Tables Test Results ===")
        results.forEach { (table, success) ->
            println("$table: ${if (success) "✓" else "✗"}")
        }
        
        return results
    }
}
