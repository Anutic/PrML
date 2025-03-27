package com.example.mycalc2.Authorization.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mycalc2.auth.SignInResult
import com.example.mycalc2.auth.SignUpResult

class LoginViewModel: ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnSignUp -> {
                when (action.result) {
                    SignUpResult.Cancelled -> {
                        state = state.copy(errorMessage = "Sign up was cancelled.")
                    }
                    SignUpResult.Failure -> {
                        state = state.copy(errorMessage = "Sign up failed.")
                    }
                    is SignUpResult.Success -> {
                        state = state.copy(loggedInUser = action.result.username)
                    }
                }
            }
            is LoginAction.OnSignIn -> {
                when (action.result) {
                    SignInResult.Cancelled -> {
                        state = state.copy(errorMessage = "Sign in was cancelled.")
                    }
                    SignInResult.Failure -> {
                        state = state.copy(errorMessage = "Sign in failed.")
                    }
                    is SignInResult.Success -> {
                        state = state.copy(loggedInUser = action.result.username)
                    }
                    is SignInResult.NoCredentials -> {
                        state = state.copy(errorMessage = "No Passkeys found. Register first.")
                    }
                }
            }
            LoginAction.OnToggleIsRegister -> {
                state = state.copy(isRegister = !state.isRegister)
            }
            is LoginAction.OnUsernameChange -> {
                state = state.copy(username = action.username)
            }
        }
    }
}