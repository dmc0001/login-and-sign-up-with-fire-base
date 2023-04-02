package com.anyandroid.haithemapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.anyandroid.haithemapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private var username: String = ""
    private var fullName: String = ""
    private lateinit var user: User
    private lateinit var userDocument : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
         userDocument = Firebase.firestore

        userDocument.collection("users").document("lolodmc69")


        binding.buttonAdd.setOnClickListener {
            username = binding.editUsername.text.toString()
            fullName = binding.editFullName.text.toString()
            user = User(username, fullName)
            if (username.isNotBlank() && !username.contains("/") && !username.contains("\\") && !username.contains(
                    "."
                )
            ) {
                lifecycleScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            userDocument.collection("users").document(username).set(user).await()
                        }
                        val documentReference = userDocument.collection("users").document(username)
                        val documentSnapshot = withContext(Dispatchers.IO) {
                            documentReference.get().await()
                        }
                        if (documentSnapshot.exists()) {
                            val userData = documentSnapshot.toObject<User>()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Successful add data",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                binding.getData.text = userData?.fullName
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "User not found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                e.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@MainActivity, "Invalid username", Toast.LENGTH_SHORT).show()
            }
        }

        mAuth = FirebaseAuth.getInstance()
        binding.buttonSignOut.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this@MainActivity, SignupActivity::class.java)
            startActivity(intent)
            this.finish()
        }
        fetchData()
    }

    private fun fetchData() {
        lifecycleScope.launch {
            try {
                val documentReference = userDocument.collection("users").document("lolodmc69")
                val documentSnapshot = withContext(Dispatchers.IO) {
                    documentReference.get().await()
                }
                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject<User>()
                    withContext(Dispatchers.Main) {
                        binding.getData.text = userData?.fullName
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}