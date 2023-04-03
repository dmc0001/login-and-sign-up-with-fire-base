package com.anyandroid.haithemapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.anyandroid.haithemapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
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
    private var userGender: String = ""
    private var fullName: String = ""
    private lateinit var userInfo: User
    private lateinit var auth: FirebaseAuth
    private lateinit var userDocument: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userDocument = Firebase.firestore
        auth = Firebase.auth
        binding.getData.text = auth.currentUser?.displayName ?: "Username"

        userDocument.collection("users").document(auth.currentUser?.uid?:"")


        binding.buttonAdd.setOnClickListener {
            userGender = binding.editUserGender.text.toString()
            fullName = binding.editFullName.text.toString()
            userInfo = User(userGender, fullName)
            saveDataUser(auth)
        }
        binding.buttonNext.setOnClickListener {
            val intent = Intent(this@MainActivity,MainActivity2::class.java)
            startActivity(intent)
        }

        mAuth = FirebaseAuth.getInstance()
        binding.buttonSignOut.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this@MainActivity, SignupActivity::class.java)
            startActivity(intent)
            this.finish()
        }
        fetchData(auth)
        updateDate(auth)

    }

    private fun saveDataUser(auth: FirebaseAuth) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    userDocument.collection("users").document(auth.currentUser?.uid ?: "")
                        .update(
                            "gender", userInfo.gender,
                            "fullName", userInfo.fullName
                        )
                        .await()
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Added data successfully :) ",
                        Toast.LENGTH_SHORT
                    )
                        .show()

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
    }



    private fun fetchData(auth: FirebaseAuth) {
        lifecycleScope.launch {
            try {
                val documentReference = userDocument.collection("users").document(auth.currentUser?.uid?:"")
                val documentSnapshot = withContext(Dispatchers.IO) {
                    documentReference.get().await()




                }
                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject<User>()
                    withContext(Dispatchers.Main) {
                        binding.tvGender.text = userData?.gender
                        binding.tvFullName.text= userData?.fullName

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
    fun updateDate(auth: FirebaseAuth){
        val userRef = userDocument.collection("users").document(auth.currentUser?.uid?:"")
        userRef.addSnapshotListener{ snapshot, e ->
            if (e != null) {
                // Handle errors here
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                val gender = snapshot.getString("gender") ?: ""
                val fullName = snapshot.getString("fullName") ?: ""
                binding.tvGender.text = gender
                binding.tvFullName.text= fullName

            }
        }

    }

}


