package org.kts.tazmin.feature.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ktskotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.kts.tazmin.feature.auth.presentation.state.LoginError
import org.kts.tazmin.feature.auth.presentation.state.LoginUiEvent
import org.kts.tazmin.feature.auth.presentation.state.LoginUiState
import org.kts.tazmin.feature.auth.presentation.viewmodel.LoginViewModel
import org.kts.tazmin.feature.auth.presentation.viewmodel.OAuthViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = koinInject(),
    oAuthViewModel: OAuthViewModel = koinInject(),
    onNavigateToMain: () -> Unit
) {
    val loginState by loginViewModel.state.collectAsStateWithLifecycle()
    val oAuthState by oAuthViewModel.state.collectAsStateWithLifecycle()

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // слушаем события формы
    LaunchedEffect(loginViewModel) {
        loginViewModel.events.collect { event ->
            when (event) {
                LoginUiEvent.LoginSuccessEvent -> {
                    onNavigateToMain()
                }
            }
        }
    }

    // слушаем OAuth успех
    LaunchedEffect(oAuthState.isAuthenticated) {
        if (oAuthState.isAuthenticated) {
            onNavigateToMain()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            // показываем WebView для OAuth
            oAuthState.showWebView -> {
                LoginWebView(
                    onCodeReceived = oAuthViewModel::onCodeReceived,
                    onError = oAuthViewModel::onError,
                    modifier = Modifier.fillMaxSize()
                )

                // кнопка закрытия WebView
                IconButton(
                    onClick = { oAuthViewModel.resetWebView() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // показываем загрузку OAuth
            oAuthState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // показываем форму логина
            else -> {
                LoginForm(
                    state = loginState,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityChange = { passwordVisible = it },
                    onUsernameChange = loginViewModel::onUsernameChanged,
                    onPasswordChange = loginViewModel::onPasswordChanged,
                    onLoginClick = loginViewModel::onLoginClick,
                    onOAuthClick = oAuthViewModel::onLoginWithStepik
                )
            }
        }
    }
}

@Composable
fun LoginForm(
    state: LoginUiState,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onOAuthClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.login_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        // форма логина
        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.login_label)) },
            placeholder = { Text(stringResource(Res.string.login_placeholder)) },
            singleLine = true,
            isError = state.error is LoginError.InvalidUserName,
            supportingText = {
                if (state.error is LoginError.InvalidUserName) {
                    Text(stringResource(Res.string.login_requirement))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.password_label)) },
            placeholder = { Text(stringResource(Res.string.password_placeholder)) },
            singleLine = true,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else PasswordVisualTransformation(),
            isError = state.error is LoginError.InvalidPassword,
            supportingText = {
                if (state.error is LoginError.InvalidPassword) {
                    Text(stringResource(Res.string.password_requirement))
                }
            },
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Спрятать пароль" else "Показать пароль"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(32.dp))

        //кнопка обычного входа
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            enabled = state.isLoginButtonActive
        ) {
            Text(stringResource(Res.string.login_button))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // кнопка OAuth входа
        OutlinedButton(
            onClick = onOAuthClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.login_with_stepik)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ошибка сервера
        if (state.error is LoginError.Server) {
            Text(
                text = state.error.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
