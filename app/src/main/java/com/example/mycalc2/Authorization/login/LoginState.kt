package com.example.mycalc2.Authorization.login

data class LoginState(
    val loggedInUser: String? = null,
    val username: String = "user",
    val errorMessage: String? = null,
    val isRegister: Boolean = false
)
