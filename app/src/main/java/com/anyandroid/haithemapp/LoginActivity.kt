package com.anyandroid.haithemapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.anyandroid.haithemapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()

        binding.txtSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener {
            login()

        }
    }

    private fun login() {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        // Launch a coroutine on the IO dispatcher
        CoroutineScope(Dispatchers.IO).launch {

            try {
                // Attempt to sign in with the provided email and password
                val authResult = mAuth.signInWithEmailAndPassword(email, password).await()

                // Check if the user is verified
                if (authResult.user?.isEmailVerified == true) {
                    // Switch to the Main dispatcher to launch the MainActivity on the UI thread
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }
                } else {
                    // User email address is not verified, prevent login
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Please, Verify your email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                // Handle any exceptions thrown during login
                Toast.makeText(this@LoginActivity, e.message.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }
}
