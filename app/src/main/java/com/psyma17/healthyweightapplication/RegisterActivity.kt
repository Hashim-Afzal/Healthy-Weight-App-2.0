package com.psyma17.healthyweightapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.psyma17.healthyweightapplication.data.UserProfileData
import com.psyma17.healthyweightapplication.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityRegisterBinding
    lateinit var auth: FirebaseAuth

    companion object {

        private val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegisterRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = binding.etEmailRegister.text.toString()
        val password = binding.etPasswordRegister.text.toString()
        val displayName = binding.etDisplayNameRegister.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty() && displayName.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                    }
                    auth.currentUser?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build()
                        it.updateProfile(profileUpdates).await()
                        saveUserProfileData()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkLoggedInState() {
        if (auth.currentUser == null) { // not logged in
            //tvLoggedInRegister.text = getString(R.string.LoginOrRegister)
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }

    private fun saveUserProfileData() = CoroutineScope(Dispatchers.IO).launch {
        val userRef = Firebase.firestore.collection("users").document(auth.currentUser?.uid.toString())

        userRef
            .update("userName", binding.etDisplayNameRegister.text.toString())
            .addOnSuccessListener { Log.d(TAG, "Display Name successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
            .await()
    }
}