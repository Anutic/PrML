package com.example.mycalc2.auth

import android.app.Activity
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.*
import org.json.JSONObject

class AccountManager(private val activity: Activity) {
    private val credentialManager = CredentialManager.create(activity)

    /**
     * Регистрация пользователя с Passkey
     */
    suspend fun registerWithPasskey(username: String): SignUpResult {
        return try {
            val requestJson = """
            {
                "rp": { "name": "MyCalc App" },
                "user": { "id": "${Base64.encodeToString(username.toByteArray(), Base64.NO_WRAP)}", "name": "$username", "displayName": "$username" },
                "pubKeyCredParams": [ { "alg": -7, "type": "public-key" } ],
                "authenticatorSelection": { "residentKey": "preferred", "userVerification": "required" },
                "attestation": "none",
                "timeout": 60000
            }
        """.trimIndent()

            val createRequest = CreatePublicKeyCredentialRequest(requestJson)
            credentialManager.createCredential(activity, createRequest) // Создание Passkey

            SignUpResult.Success(username)
        } catch (e: CreateCredentialCancellationException) {
            Log.e("PassKeyAuth", "Registration cancelled", e)
            SignUpResult.Cancelled
        } catch (e: CreateCredentialException) {
            Log.e("PassKeyAuth", "Registration failed", e)
            SignUpResult.Failure
        }
    }

    /**
     * Вход через Passkey
     */
    suspend fun signInWithPasskey(): SignInResult {
        return try {
            val requestJson = """{ "userVerification": "required" }"""
            val getRequest = GetCredentialRequest(listOf(GetPublicKeyCredentialOption(requestJson)))
            val credentialResponse = credentialManager.getCredential(activity, getRequest) // Получение Passkey

            val credential = credentialResponse.credential as? PublicKeyCredential
                ?: return SignInResult.Failure

            val responseJson = credential.authenticationResponseJson
            val jsonResponse = JSONObject(responseJson)

            val username = jsonResponse.getString("userHandle") // Получаем имя пользователя

            SignInResult.Success(username)
        } catch (e: GetCredentialCancellationException) {
            SignInResult.Cancelled
        } catch (e: NoCredentialException) {
            SignInResult.NoCredentials
        } catch (e: GetCredentialException) {
            SignInResult.Failure
        }
    }
}
