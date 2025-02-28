package com.example.something.viewmodel


import androidx.lifecycle.ViewModel



import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<Boolean?>(null)
    val authState: StateFlow<Boolean?> = _authState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = true
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _authState.value = false
                _isAuthenticated.value = false
                _errorMessage.value = e.message
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = true
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _authState.value = false
                _isAuthenticated.value = false
                _errorMessage.value = e.message
            }
        }
    }

    fun guestSignIn() {
        viewModelScope.launch {
            try {
                auth.signInAnonymously().await()
                _authState.value = true
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _authState.value = false
                _isAuthenticated.value = false
                _errorMessage.value = e.message
            }
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = false
        _isAuthenticated.value = false
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
}
