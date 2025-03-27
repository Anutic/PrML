package com.example.mycalc2.Authorization.login

import com.example.mycalc2.auth.SignInResult
import com.example.mycalc2.auth.SignUpResult
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mycalc2.auth.AccountManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    onLoggedIn: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val accountManager = remember {
        AccountManager(context as ComponentActivity)
    }

    LaunchedEffect(key1 = true) {
        val result = accountManager.signInWithPasskey()
        onAction(LoginAction.OnSignIn(result))
    }

    LaunchedEffect(key1 = state.loggedInUser) {
        if (state.loggedInUser != null) {
            onLoggedIn(state.loggedInUser)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        TextField(
            value = state.username,
            onValueChange = {
                onAction(LoginAction.OnUsernameChange(it))
            },
            label = { Text(text = "Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Row {
            Text(text = "Register")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = state.isRegister,
                onCheckedChange = {
                    onAction(LoginAction.OnToggleIsRegister)
                }
            )
        }

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(onClick = {
            scope.launch {
                if (state.isRegister) {
                    val result = accountManager.registerWithPasskey(state.username)
                    onAction(LoginAction.OnSignUp(result))
                } else {
                    val result = accountManager.signInWithPasskey()
                    onAction(LoginAction.OnSignIn(result))
                }
            }
        }) {
            Text(text = if (state.isRegister) "Register with Passkey" else "Login with Passkey")
        }
    }
}
