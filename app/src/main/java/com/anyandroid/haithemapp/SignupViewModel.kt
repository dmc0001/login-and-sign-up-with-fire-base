package com.anyandroid.haithemapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class SignupViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    val signUpSuccess = MutableLiveData<Boolean>()
    val signUpFailure = MutableLiveData<String>()
    fun registerUser(email: String, password: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                signUpSuccess.postValue(true)

            } catch (e: Exception) {
                signUpFailure.postValue(e.localizedMessage)
            }

        }
    }
}