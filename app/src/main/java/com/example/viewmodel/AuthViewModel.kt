package com.example.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import com.example.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthUiState(
    val user: com.google.firebase.auth.FirebaseUser? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(AuthUiState(user = auth.currentUser, isLoading = false))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _uiState.value = AuthUiState(user = firebaseAuth.currentUser, isLoading = false)
    }

    init { auth.addAuthStateListener(authListener) }

    fun signIn(email: String, password: String) {
        if (!validate(email, password)) return
        _uiState.value = AuthUiState(isLoading = true)
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnFailureListener { error -> showError(error.message) }
    }

    fun signUp(displayName: String, email: String, password: String) {
        if (displayName.trim().isEmpty()) { showError("Enter a display name"); return }
        if (!validate(email, password)) return
        _uiState.value = AuthUiState(isLoading = true)
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val profile = UserProfileChangeRequest.Builder().setDisplayName(displayName.trim()).build()
                result.user?.let { user ->
                    user.updateProfile(profile)
                    firestore.collection("users").document(user.uid).set(
                        mapOf(
                            "uid" to user.uid,
                            "displayName" to displayName.trim(),
                            "countryCode" to "",
                            "avatarUrl" to "",
                            "level" to 1L,
                            "xp" to 0L,
                            "stars" to 0L,
                            "publicProfile" to true
                        )
                    )
                }
            }
            .addOnFailureListener { error -> showError(error.message) }
    }

    fun signOut() = auth.signOut()

    fun googleSignInIntent(context: Context): Intent = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.example.R.string.default_web_client_id))
            .requestEmail()
            .build()
    ).signInIntent

    fun handleGoogleSignInResult(result: ActivityResult) {
        // User closed the account chooser without selecting anything.
        if (result.resultCode == android.app.Activity.RESULT_CANCELED && result.data == null) {
            logD("GoogleSignIn: user cancelled (RESULT_CANCELED, no data)")
            showError("Google sign-in was cancelled.")
            return
        }

        if (result.resultCode != android.app.Activity.RESULT_OK || result.data == null) {
            // A non-OK result that carries data usually embeds the real Google status code.
            val apiEx = try {
                if (result.data != null) {
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    null
                } else null
            } catch (e: ApiException) { e } catch (_: Exception) { null }
            if (apiEx is ApiException) {
                showError(mapGoogleSignInError(apiEx))
            } else {
                logW("GoogleSignIn: failed resultCode=${result.resultCode}, data=${if (result.data == null) "null" else "present"}")
                showError("Google Sign-In failed. Please try again later.")
            }
            return
        }

        _uiState.value = AuthUiState(isLoading = true)
        GoogleSignIn.getSignedInAccountFromIntent(result.data)
            .addOnSuccessListener { account ->
                logD("GoogleSignIn account: id=${account.id}, email=${account.email}, idTokenPresent=${!account.idToken.isNullOrBlank()}")
                val idToken = account.idToken
                if (idToken.isNullOrBlank()) {
                    logW("GoogleSignIn: account returned but idToken is blank (check Web OAuth client / OAuth consent screen)")
                    showError("Google Sign-In configuration error. Please try again later.")
                    return@addOnSuccessListener
                }
                firebaseAuthWithGoogle(idToken, account.email, account.displayName, account.photoUrl?.toString())
            }
            .addOnFailureListener { error -> showError(mapGoogleSignInError(error as? ApiException)) }
    }

    private fun firebaseAuthWithGoogle(idToken: String, email: String?, displayName: String?, photoUrl: String?) {
        // SECURITY: the idToken is intentionally never written to logs.
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                logD("Firebase sign-in result: uid=${user?.uid}, isNewUser=${authResult.additionalUserInfo?.isNewUser}")
                if (user == null) {
                    logW("Firebase sign-in succeeded but FirebaseUser is null")
                    showError("Google Sign-In configuration error. Please try again later.")
                    return@addOnSuccessListener
                }
                createOrUpdateUser(user, displayName, photoUrl)
            }
            .addOnFailureListener { error -> showError(mapFirebaseError(error)) }
    }

    private fun createOrUpdateUser(user: FirebaseUser, displayName: String?, photoUrl: String?) {
        val data = mapOf(
            "uid" to user.uid,
            "displayName" to (displayName ?: user.displayName ?: "Player"),
            "countryCode" to "",
            "avatarUrl" to (photoUrl ?: user.photoUrl?.toString() ?: ""),
            "level" to 1L,
            "xp" to 0L,
            "stars" to 0L,
            "publicProfile" to true
        )
        firestore.collection("users").document(user.uid).set(data, SetOptions.merge())
            .addOnFailureListener { error ->
                logW("Firebase: failed to create/update users/${user.uid}", error)
            }
    }

    private fun mapGoogleSignInError(exception: ApiException?): String {
        val statusCode = exception?.statusCode ?: -1
        val statusMessage = exception?.statusMessage
        logW("GoogleSignIn error -> statusCode=$statusCode, statusMessage=$statusMessage, type=${exception?.javaClass?.name}", exception)
        return when {
            exception == null -> "Google Sign-In failed. Please try again later."
            statusCode == STATUS_CANCELLED -> "Google sign-in was cancelled."
            statusCode == STATUS_DEVELOPER_ERROR ->
                "Google Sign-In configuration error. Please try again later." // SHA-1 / package name / OAuth client misconfig
            statusCode == STATUS_SIGN_IN_FAILED ->
                "Google Sign-In failed. Please try again later."
            statusCode == CommonStatusCodes.NETWORK_ERROR || statusCode == CommonStatusCodes.TIMEOUT ->
                "Unable to connect. Check your internet connection."
            statusCode == STATUS_INVALID_ACCOUNT ->
                "Google Sign-In configuration error. Please try again later."
            else -> "Google Sign-In failed. Please try again later."
        }
    }

    private fun mapFirebaseError(error: Exception?): String {
        val fe = error as? FirebaseAuthException
        val code = fe?.errorCode
        val message = fe?.message
        logE("FirebaseAuth error -> code=$code, message=$message, type=${error?.javaClass?.name}", error)
        return when (code) {
            "ERROR_INVALID_CREDENTIAL", "ERROR_INVALID_ID_TOKEN", "ERROR_INVALID_MESSAGE" ->
                "Google Sign-In configuration error. Please try again later."
            "ERROR_NETWORK_REQUEST_FAILED", "ERROR_NETWORK" ->
                "Unable to connect. Check your internet connection."
            else -> "Google Sign-In failed. Please try again later."
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }

    private fun showError(message: String?) {
        _uiState.value = _uiState.value.copy(errorMessage = message ?: "Something went wrong", isLoading = false)
    }

    private companion object {
        const val TAG = "AuthViewModel"
        const val STATUS_CANCELLED = GoogleSignInStatusCodes.SIGN_IN_CANCELLED
        const val STATUS_DEVELOPER_ERROR = GoogleSignInStatusCodes.DEVELOPER_ERROR
        const val STATUS_SIGN_IN_FAILED = GoogleSignInStatusCodes.SIGN_IN_FAILED
        const val STATUS_INVALID_ACCOUNT = GoogleSignInStatusCodes.INVALID_ACCOUNT
    }

    private fun logD(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, message)
    }

    private fun logW(message: String, tr: Throwable? = null) {
        Log.w(TAG, message, tr)
    }

    private fun logE(message: String, tr: Throwable? = null) {
        Log.e(TAG, message, tr)
    }

    private fun validate(email: String, password: String): Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) { showError("Enter a valid email address"); return false }
        if (password.length < 6) { showError("Password must be at least 6 characters"); return false }
        return true
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }
}