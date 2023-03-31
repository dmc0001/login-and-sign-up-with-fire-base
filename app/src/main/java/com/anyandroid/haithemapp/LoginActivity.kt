package com.anyandroid.haithemapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.anyandroid.haithemapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mAuth = FirebaseAuth.getInstance()

        binding.txtSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity,SignupActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        if (user != null && user.isEmailVerified) {
                            val intent = Intent(this@LoginActivity,MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // User email address is not verified, prevent login
                            Toast.makeText(this@LoginActivity,"Please, Verify your email",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // User login failed
                    }
                }
        }
    }
}