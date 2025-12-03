// ForgotPasswordScreen.kt – HOÀN CHỈNH CUỐI CÙNG (ĐÃ FIX HẾT LỖI)
package com.example.medinotify.ui.screens.auth.password

import android.widget.Toast
import androidx.compose.foundation.clickable                  // ← ĐÃ THÊM DÒNG NÀY!
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack  // ← DÙNG AutoMirrored (không lỗi deprecated)
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
fun ForgotPasswordRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSendCode: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.navigateNext) {
        if (uiState.navigateNext) {
            Toast.makeText(context, "Đã gửi mã xác minh đến email của bạn", Toast.LENGTH_LONG).show()
            viewModel.onNavigationHandled()
            onSendCode()
        }
    }

    ForgotPasswordScreen(
        modifier = modifier,
        state = uiState,
        onBack = onBack,
        onEmailChanged = viewModel::onEmailChanged,
        onSendCode = viewModel::sendCode
    )
}

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    state: ForgotPasswordUiState,
    onBack: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onSendCode: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Back button – ĐÃ FIX: dùng AutoMirrored
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Quay lại",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Quên mật khẩu?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Đừng lo! Chỉ cần nhập email của bạn, chúng tôi sẽ gửi mã xác minh để bạn đặt lại mật khẩu.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        AuthTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            label = "Email của bạn",
            modifier = Modifier.fillMaxWidth()
        )

        state.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            label = "Gửi mã xác minh",
            modifier = Modifier.fillMaxWidth(),
            enabled = state.email.isNotBlank() && !state.isLoading,
            isLoading = state.isLoading,
            onClick = onSendCode
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Đã nhớ mật khẩu?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Quay lại đăng nhập",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clickable { onBack() },  // ← BÂY GIỜ ĐÃ HOẠT ĐỘNG!
            textAlign = TextAlign.Center
        )
    }
}