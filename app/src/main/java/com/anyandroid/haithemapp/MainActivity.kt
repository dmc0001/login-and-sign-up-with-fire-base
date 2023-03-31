package com.anyandroid.haithemapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anyandroid.haithemapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

         mAuth = FirebaseAuth.getInstance()

        binding.buttonSignOut.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this@MainActivity, SignupActivity::class.java)
            startActivity(intent)
            this.finish()
        }

    }
}