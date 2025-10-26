package hu.ait.finalproject.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.finalproject.R

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.background(Color(204, 225, 255))) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.app_logo),
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.padding(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(.8f),
                label = { Text(text = stringResource(R.string.e_mail)) },
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, null) }
            )
            Spacer(modifier = Modifier.padding(8.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(.8f),
                label = { Text(text = stringResource(R.string.password)) },
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Info, null) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        if (showPassword) {
                            Icon(Icons.Default.Add, null)
                        } else {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.padding(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton( onClick = {
                    coroutineScope.launch {
                        val result = viewModel.loginUser(email, password)
                        if (result?.user != null) {
                            onLoginSuccess()
                        }
                    }
                }) { Text(text = stringResource(R.string.login), fontFamily = FontFamily.Monospace) }

                OutlinedButton(onClick = {
                    viewModel.registerUser(email, password)
                }) { Text(text = stringResource(R.string.register), fontFamily = FontFamily.Monospace) }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) { when (viewModel.loginUiState) {
            is LoginUiState.Init -> {}
            is LoginUiState.Error -> {
                Text("Error: ${
                    (viewModel.loginUiState as LoginUiState.Error).errorMessage
                }")
            }
            is LoginUiState.Loading -> CircularProgressIndicator()
            is LoginUiState.LoginSuccess -> Text(stringResource(R.string.login_ok))
            is LoginUiState.RegisterSuccess -> Text(stringResource(R.string.registration_ok))
            }
        }
    }
}