package com.example.mycalc2.Authorization.login

import com.example.mycalc2.auth.SignInResult
import com.example.mycalc2.auth.SignUpResult

sealed interface LoginAction {
    data class OnSignIn(val result: SignInResult): LoginAction
    data class OnSignUp(val result: SignUpResult): LoginAction
    data class OnUsernameChange(val username: String): LoginAction
//    data class OnPasswordChange(val password: String): LoginAction
    data object OnToggleIsRegister: LoginAction
}