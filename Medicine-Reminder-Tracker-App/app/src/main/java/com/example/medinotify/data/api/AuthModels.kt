package com.example.medinotify.data.api

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)
data class GoogleLoginRequest(val idToken: String)
data class SaveTokenRequest(val userId: String, val fcmToken: String)
data class ForgotPasswordRequest(val email: String)
data class VerifyCodeRequest(val email: String, val code: String)
data class ResetPasswordRequest(val email: String, val code: String, val newPassword: String)
data class LoginResponse(
    val success: Boolean,
    val userId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val message: String? = null
)