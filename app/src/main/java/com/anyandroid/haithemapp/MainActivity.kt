package com.anyandroid.haithemapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.anyandroid.haithemapp.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth

import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers

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
    private var pdp: Uri? = null
    private var user:FirebaseUser?=null
    private var profileUpdates: UserProfileChangeRequest ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userDocument = Firebase.firestore
        auth = Firebase.auth
        binding.getData.text = "Username : ${auth.currentUser?.displayName}"

       Glide.with(this)
            .load(auth.currentUser?.photoUrl)
            .error(R.drawable.baseline_person_24) // Image to display on error
            .circleCrop() // Crop the image into a circle
            .into(binding.editUserPhoto2)
        userDocument.collection("users").document(auth.currentUser?.uid ?: "")


        binding.buttonSave.setOnClickListener {
            userGender = binding.editUserGender.text.toString()
            fullName = binding.editFullName.text.toString()
            userInfo = User(userGender, fullName)

            saveDataUser(auth)

            Glide.with(this)
                .load(auth.currentUser?.photoUrl)
                .error(R.drawable.baseline_person_24) // Image to display on error
                .circleCrop() // Crop the image into a circle
                .into(binding.editUserPhoto2)
        }
        binding.editUserPhoto.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "*image/*"
            startActivityForResult(intent, 1)
        }
        binding.buttonNext.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            startActivity(intent)
        }

        mAuth = FirebaseAuth.getInstance()
        binding.buttonSignOut.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this@MainActivity, SignupActivity::class.java)
            startActivity(intent)
            this.finish()
        }
        updateDate(auth)

    }


   private fun saveDataUser(auth: FirebaseAuth) {
       lifecycleScope.launch {
           try {
               withContext(Dispatchers.IO) {
                   // Update Firestore document with only the changed fields
                   val updateData = hashMapOf<String, Any?>()
                   if (!userInfo.gender.isNullOrEmpty()) {
                       updateData["gender"] = userInfo.gender
                   }
                   if (!userInfo.fullName.isNullOrEmpty()) {
                       updateData["fullName"] = userInfo.fullName
                   }

                   userDocument.collection("users").document(auth.currentUser?.uid ?: "")
                       .update(updateData)
                       .await()


                       val photoUri = Uri.parse(pdp.toString())
                       profileUpdates =
                           UserProfileChangeRequest.Builder().setPhotoUri(photoUri).build()

                   auth.currentUser?.updateProfile(profileUpdates!!)?.await()

               }
               withContext(Dispatchers.Main) {
                   // Update user's profile photo if changed
                   //profileUpdates?.let { auth.currentUser?.updateProfile(it)?.await() }
                   Toast.makeText(
                       this@MainActivity,
                       "Updated data successfully :) ",
                       Toast.LENGTH_SHORT
                   ).show()
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
                val documentReference =
                    userDocument.collection("users").document(auth.currentUser?.uid ?: "")
                val documentSnapshot = withContext(Dispatchers.IO) {
                    documentReference.get().await()


                }
                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject<User>()
                    withContext(Dispatchers.Main) {
                        binding.tvGender.text = userData?.gender
                        binding.tvFullName.text = userData?.fullName

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            pdp = data.data!!
            binding.editUserPhoto.setImageURI(pdp)

        }
    }

}


