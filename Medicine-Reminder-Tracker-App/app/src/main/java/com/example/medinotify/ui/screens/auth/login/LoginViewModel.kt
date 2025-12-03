package com.example.medinotify.ui.screens.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medinotify.data.auth.AuthRepository
import com.example.medinotify.data.auth.AuthResult
import com.example.medinotify.data.auth.FirebaseAuthRepository   // ← Chỉ dùng để lấy idToken Google
import com.example.medinotify.data.auth.PhpAuthRepository       // ← Xử lý tất cả login PHP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel(
    private val firebaseRepo: AuthRepository = FirebaseAuthRepository(), // Chỉ dùng lấy idToken Google
    private val phpRepo: AuthRepository = PhpAuthRepository()           // Tất cả login đều qua PHP
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    // ĐĂNG NHẬP EMAIL + PASS → DÙNG PHP BACKEND
    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        val validationError = when {
            email.isBlank() || password.isBlank() -> "Vui lòng nhập đầy đủ email và mật khẩu."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email chưa đúng định dạng."
            password.length < 6 -> "Mật khẩu cần ít nhất 6 ký tự."
            else -> null
        }

        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = phpRepo.signIn(email, password)) {  // ← DÙNG PHP
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                is AuthResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    // ĐĂNG NHẬP GOOGLE → Firebase lấy idToken → gửi lên PHP xử lý
    fun loginWithGoogle(idToken: String?) {
        if (idToken.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Không lấy được thông tin từ Google. Vui lòng thử lại.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = phpRepo.signInWithGoogle(idToken)) {  // ← Gửi idToken lên PHP
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                is AuthResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun onSuccessConsumed() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun onExternalLoginError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }
}