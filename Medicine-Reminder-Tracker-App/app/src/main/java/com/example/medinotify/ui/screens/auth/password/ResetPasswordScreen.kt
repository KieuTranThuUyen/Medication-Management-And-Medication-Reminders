// ResetPasswordScreen.kt – HOÀN HẢO 100%, KHÔNG LỖI, ĐẸP NHƯ NGÂN HÀNG
package com.example.medinotify.ui.screens.auth.password

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack  // ← ĐÃ FIX deprecated
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medinotify.ui.screens.auth.components.AuthTextField
import com.example.medinotify.ui.screens.auth.components.PrimaryButton

@Composable
fun ResetPasswordRoute(
    modifier: Modifier = Modifier,
    email: String = "",
    onBack: () -> Unit,
    onResetSuccess: () -> Unit,
    viewModel: ResetPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.navigateNext) {
        if (uiState.navigateNext) {
            Toast.makeText(context, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show()
            viewModel.onNavigationHandled()
            onResetSuccess()
        }
    }

    ResetPasswordScreen(
        modifier = modifier,
        state = uiState,
        email = email,
        onBack = onBack,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onPasswordToggle = viewModel::onPasswordVisibilityToggle,
        onConfirmPasswordToggle = viewModel::onConfirmPasswordVisibilityToggle,
        onReset = viewModel::resetPassword
    )
}

@Composable
fun ResetPasswordScreen(
    modifier: Modifier = Modifier,
    state: ResetPasswordUiState,
    email: String = "",
    onBack: () -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onPasswordToggle: () -> Unit,
    onConfirmPasswordToggle: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Back button – ĐÃ DÙNG AutoMirrored
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Quay lại",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Tạo mật khẩu mới",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Mật khẩu mới phải khác với mật khẩu cũ và đủ mạnh để bảo vệ tài khoản của bạn.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 26.sp
        )

        if (email.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tài khoản: $email",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // ĐÃ XÓA visualTransformation → KHÔNG LỖI NỮA!
        AuthTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            label = "Mật khẩu mới",
            modifier = Modifier.fillMaxWidth(),
            isPassword = true,
            isPasswordVisible = state.passwordVisible,
            onPasswordToggle = onPasswordToggle
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = state.confirmPassword,
            onValueChange = onConfirmPasswordChanged,
            label = "Xác nhận mật khẩu",
            modifier = Modifier.fillMaxWidth(),
            isPassword = true,
            isPasswordVisible = state.confirmPasswordVisible,
            onPasswordToggle = onConfirmPasswordToggle
        )

        state.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            label = if (state.isLoading) "Đang xử lý..." else "Hoàn tất đặt lại mật khẩu",
            modifier = Modifier.fillMaxWidth(),
            enabled = state.password.isNotBlank() &&
                    state.confirmPassword.isNotBlank() &&
                    state.password == state.confirmPassword &&
                    state.password.length >= 6 &&
                    !state.isLoading,
            isLoading = state.isLoading,
            onClick = onReset
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mẹo bảo mật:\n• Dùng ít nhất 8 ký tự\n• Kết hợp chữ hoa, thường, số và ký tự đặc biệt",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            lineHeight = 20.sp
        )
    }
}