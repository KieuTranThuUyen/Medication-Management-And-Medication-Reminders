// data/api/RegisterRequest.kt
package com.example.medinotify.data.api

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String = ""        // ← THÊM DÒNG NÀY
)