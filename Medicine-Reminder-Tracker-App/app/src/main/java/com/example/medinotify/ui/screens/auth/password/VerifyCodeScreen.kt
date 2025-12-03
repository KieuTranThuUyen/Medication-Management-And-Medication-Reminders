// VerifyCodeScreen.kt – HOÀN HẢO TUYỆT ĐỐI 2025 (ĐÃ FIX HẾT)
package com.example.medinotify.ui.screens.auth.password

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medinotify.ui.screens.auth.components.PrimaryButton

@Composable
fun VerifyCodeRoute(
    modifier: Modifier = Modifier,
    email: String,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    viewModel: VerifyCodeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.navigateNext) {
        if (uiState.navigateNext) {
            Toast.makeText(context, "Xác minh thành công!", Toast.LENGTH_SHORT).show()
            viewModel.onNavigationHandled()
            onConfirm()
        }
    }

    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onInfoMessageShown()
        }
    }

    VerifyCodeScreen(
        modifier = modifier,
        state = uiState,
        email = email,
        onBack = onBack,
        onCodeChanged = viewModel::onCodeChanged,
        onConfirm = viewModel::confirmCode,
        onResendCode = viewModel::resendCode   // ĐÃ SỬA: DÙNG :: → KHÔNG LỖI!
    )
}

// Phần VerifyCodeScreen giữ nguyên 100% như bạn đã viết – ĐẸP HOÀN HẢO!
@Composable
fun VerifyCodeScreen(
    modifier: Modifier = Modifier,
    state: VerifyCodeUiState,
    email: String,
    onBack: () -> Unit,
    onCodeChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onResendCode: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Quay lại",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Nhập mã xác minh",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Chúng tôi đã gửi mã gồm 6 chữ số đến",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        BasicTextField(
            value = state.code,
            onValueChange = onCodeChanged,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            cursorBrush = SolidColor(Color.Transparent),
            textStyle = MaterialTheme.typography.titleLarge.copy(color = Color.Transparent),
            decorationBox = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(OTP_CODE_LENGTH) { index ->
                        val char = state.code.getOrNull(index)?.toString().orEmpty()
                        val isFilled = char.isNotEmpty()

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .border(
                                    width = if (isFilled) 2.dp else 1.5.dp,
                                    color = if (isFilled) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(
                                    color = if (isFilled)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { focusRequester.requestFocus() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        if (index < OTP_CODE_LENGTH - 1) {
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
            }
        )

        state.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        PrimaryButton(
            label = "Xác nhận mã",
            modifier = Modifier.fillMaxWidth(),
            enabled = state.code.length == OTP_CODE_LENGTH && !state.isLoading,
            isLoading = state.isLoading,
            onClick = onConfirm
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Chưa nhận được mã?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Gửi lại",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (state.isLoading)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.clickable(
                    enabled = !state.isLoading,
                    onClick = onResendCode
                )
            )
        }
    }
}