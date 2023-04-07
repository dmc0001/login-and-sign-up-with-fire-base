package com.anyandroid.haithemapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.anyandroid.haithemapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.buttonSignUp.setOnClickListener {

            registerUser()

        }
        binding.txtLogin.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    private fun registerUser() {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        val username = binding.editTextName.text.toString()

        // Launch a coroutine on the IO dispatcher
        CoroutineScope(Dispatchers.IO).launch {

            try {
                // Attempt to create a user with the provided email and password

                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()

                user?.updateProfile(profileUpdates)?.await()
                val userMap = hashMapOf(
                    "email" to email,
                    "username" to username
                )
                var userDocument:FirebaseFirestore = Firebase.firestore
                userDocument.collection("users").document(user?.uid?:"").set(userMap).await()


                // Send email verification to the user
                authResult.user?.sendEmailVerification()?.await()

                // Switch to the Main dispatcher to show a toast message on the UI thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SignupActivity,
                        "Please, Verify your email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // Launch the LoginActivity after successful user creation
                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))

            } catch (e: Exception) {
                // Handle any exceptions thrown during user creation
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignupActivity, e.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


}

