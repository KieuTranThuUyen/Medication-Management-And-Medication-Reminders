// RegisterScreen.kt – HOÀN CHỈNH VỚI PHP BACKEND
package com.example.medinotify.ui.screens.auth.register

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack  // ← FIX deprecated nếu cần
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medinotify.ui.screens.auth.components.AuthTextField
import com.example.medinotify.ui.screens.auth.components.GoogleButton
import com.example.medinotify.ui.screens.auth.components.PrimaryButton
import com.example.medinotify.ui.theme.MedinotifyTheme

@Composable
fun RegisterRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onLogin: () -> Unit,
    onGoogleRegister: () -> Unit,  // Nếu Google cho đăng ký
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.navigateToLogin) {
        if (uiState.navigateToLogin) {
            Toast.makeText(context, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show()
            viewModel.onNavigationHandled()
            onRegisterSuccess()
        }
    }

    RegisterScreen(
        modifier = modifier,
        state = uiState,
        onBack = onBack,
        onNameChanged = viewModel::onNameChanged,
        onEmailChanged = viewModel::onEmailChanged,
        onPhoneChanged = viewModel::onPhoneChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onPasswordVisibilityToggle = viewModel::onPasswordVisibilityToggle,
        onConfirmPasswordVisibilityToggle = viewModel::onConfirmPasswordVisibilityToggle,
        onRegister = viewModel::register,
        onGoogleRegister = onGoogleRegister,
        onLogin = onLogin
    )
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    state: RegisterUiState,
    onBack: () -> Unit,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onConfirmPasswordVisibilityToggle: () -> Unit,
    onRegister: () -> Unit,
    onGoogleRegister: () -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Quay lại",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tạo tài khoản mới",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Vui lòng nhập thông tin để đăng ký",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AuthTextField(
                value = state.name,
                onValueChange = onNameChanged,
                label = "Họ và tên"
            )
            AuthTextField(
                value = state.email,
                onValueChange = onEmailChanged,
                label = "Email"
            )
            AuthTextField(
                value = state.phone,
                onValueChange = onPhoneChanged,
                label = "Số điện thoại"
            )
            AuthTextField(
                value = state.password,
                onValueChange = onPasswordChanged,
                label = "Mật khẩu",
                isPassword = true,
                isPasswordVisible = state.passwordVisible,
                onPasswordToggle = onPasswordVisibilityToggle
            )
            AuthTextField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChanged,
                label = "Xác nhận mật khẩu",
                isPassword = true,
                isPasswordVisible = state.confirmPasswordVisible,
                onPasswordToggle = onConfirmPasswordVisibilityToggle
            )
        }

        state.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            label = "Đăng ký",
            enabled = !state.isLoading,
            isLoading = state.isLoading,
            onClick = onRegister
        )

        Spacer(modifier = Modifier.height(24.dp))

        Divider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        GoogleButton(
            label = "Đăng ký với Google",
            onClick = onGoogleRegister
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Đã có tài khoản? ",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Đăng nhập",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.clickable(onClick = onLogin)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreview() {
    MedinotifyTheme {
        RegisterScreen(
            state = RegisterUiState(),
            onBack = {},
            onNameChanged = {},
            onEmailChanged = {},
            onPhoneChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onPasswordVisibilityToggle = {},
            onConfirmPasswordVisibilityToggle = {},
            onRegister = {},
            onGoogleRegister = {},
            onLogin = {}
        )
    }
}