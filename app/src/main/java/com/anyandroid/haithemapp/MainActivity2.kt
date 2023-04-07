package com.anyandroid.haithemapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anyandroid.haithemapp.databinding.ActivityMain2Binding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var userDocument: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        userDocument = Firebase.firestore
        auth = Firebase.auth
        binding.getData.text = "Username : ${auth.currentUser?.displayName}"

        Glide.with(this)
            .load(auth.currentUser?.photoUrl)
            .error(R.drawable.baseline_person_24) // Image to display on error
            .circleCrop() // Crop the image into a circle
            .into(binding.editUserPhoto2)

        binding.buttonBack.setOnClickListener {
            finish()
        }
        updateDate(auth)

    }
    private fun updateDate(auth: FirebaseAuth) {
        val userRef = userDocument.collection("users").document(auth.currentUser?.uid ?: "")
        userRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle errors here
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                val gender = snapshot.getString("gender") ?: ""
                val fullName = snapshot.getString("fullName") ?: ""
                binding.tvGender.text ="Gender : $gender"
                binding.tvFullName.text = "Full name : $fullName"


            }
        }

    }
}