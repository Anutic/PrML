package com.example.mycalc2.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mycalc2.R
import kotlinx.coroutines.launch
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import com.example.mycalc2.MainActivity
import android.widget.Toast

class PassKeyAuthActivity : AppCompatActivity() {
    private lateinit var accountManager: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)


        accountManager = AccountManager(this)

        // Находим кнопки по ID
        val signInButton = findViewById<Button>(R.id.signInButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Устанавливаем обработчики нажатий
        signInButton.setOnClickListener {
            lifecycleScope.launch {
                val result = accountManager.signInWithPasskey()
                handleSignInResult(result)
            }
        }

        registerButton.setOnClickListener {
            lifecycleScope.launch {
                val result = accountManager.registerWithPasskey("username") // Замените "username" на реальное имя пользователя
                handleSignUpResult(result)
            }
        }
    }

    private fun handleSignInResult(result: SignInResult) {
        when (result) {
            is SignInResult.Success -> {
                // Переход на главный экран
                navigateToMainActivity()
            }
            SignInResult.Cancelled -> {
                // Показ сообщения об отмене
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show()
            }
            SignInResult.Failure -> {
                // Показ сообщения об ошибке
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
            }
            SignInResult.NoCredentials -> {
                // Показ сообщения о необходимости регистрации
                Toast.makeText(this, "No credentials found. Please register first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignUpResult(result: SignUpResult) {
        when (result) {
            is SignUpResult.Success -> {
                // Переход на главный экран
                navigateToMainActivity()
            }
            SignUpResult.Cancelled -> {
                // Показ сообщения об отмене
                Toast.makeText(this, "Sign up cancelled", Toast.LENGTH_SHORT).show()
            }
            SignUpResult.Failure -> {
                // Показ сообщения об ошибке
                Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMainActivity() {
        // Переход на главный экран
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}