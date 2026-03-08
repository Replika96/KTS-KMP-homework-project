package org.kts.tazmin.feature.auth.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.kts.tazmin.theme.CatTheme
import ktskotlinproject.composeapp.generated.resources.Res
import ktskotlinproject.composeapp.generated.resources.login_button
import ktskotlinproject.composeapp.generated.resources.login_label
import ktskotlinproject.composeapp.generated.resources.login_placeholder
import ktskotlinproject.composeapp.generated.resources.login_requirement
import ktskotlinproject.composeapp.generated.resources.login_title
import ktskotlinproject.composeapp.generated.resources.password_label
import ktskotlinproject.composeapp.generated.resources.password_placeholder
import ktskotlinproject.composeapp.generated.resources.password_requirement
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.kts.tazmin.feature.auth.presentation.state.LoginError
import org.kts.tazmin.feature.auth.presentation.state.LoginUiEvent
import org.kts.tazmin.feature.auth.presentation.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel  = koinInject(),
    onNavigateToMain: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel){
        viewModel.events.collect{ event ->
            when(event){
                LoginUiEvent.LoginSuccessEvent -> {
                    onNavigateToMain()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.login_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = state.username,
            onValueChange = viewModel::onUsernameChanged,
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(stringResource(Res.string.password_label))
            },
            placeholder = {
                Text(stringResource(Res.string.password_placeholder))
            },
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
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Спрятать пароль" else "Показать пароль"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = viewModel::onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            enabled = state.isLoginButtonActive
        ) {
            Text(stringResource(Res.string.login_button))
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (state.error is LoginError.Server) {
            Text(
                text = (state.error as LoginError.Server).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview(){
    CatTheme {
        LoginScreen(
            onNavigateToMain = { }
        )
    }
}
