// data/auth/HybridAuthRepository.kt
package com.example.medinotify.data.auth

import com.example.medinotify.data.api.ApiService        // ĐÚNG PACKAGE
import com.example.medinotify.data.api.GoogleLoginRequest // ĐÚNG
import com.example.medinotify.data.api.LoginRequest
import com.example.medinotify.data.api.ApiClient           // DÙNG CHUNG BASE URL

class HybridAuthRepository : AuthRepository {

    private val firebaseRepo = FirebaseAuthRepository()
    private val phpApi = ApiClient.api   // DÙNG CHUNG ApiClient → không cần tạo Retrofit mới

    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val resp = phpApi.login(LoginRequest(email, password))
            if (resp.isSuccessful && resp.body()?.success == true) {
                AuthResult.Success(resp.body()!!.userId!!)
            } else {
                AuthResult.Error(resp.body()?.message ?: "Email hoặc mật khẩu sai")
            }
        } catch (e: Exception) {
            AuthResult.Error("Lỗi kết nối: ${e.message}")
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        // B1: Xác thực idToken bằng Firebase (an toàn tuyệt đối)
        val firebaseResult = firebaseRepo.signInWithGoogle(idToken)
        if (firebaseResult !is AuthResult.Success) return firebaseResult

        // B2: Gửi idToken đã xác thực lên PHP backend
        return try {
            val resp = phpApi.googleLogin(GoogleLoginRequest(idToken))
            if (resp.isSuccessful && resp.body()?.success == true) {
                AuthResult.Success(resp.body()!!.userId!!)
            } else {
                AuthResult.Error(resp.body()?.message ?: "Không thể tạo tài khoản")
            }
        } catch (e: Exception) {
            AuthResult.Error("Lỗi mạng: ${e.message}")
        }
    }
}