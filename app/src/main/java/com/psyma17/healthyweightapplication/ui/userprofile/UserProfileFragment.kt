package com.psyma17.healthyweightapplication.ui.userprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.psyma17.healthyweightapplication.R
import com.psyma17.healthyweightapplication.data.UserProfileData
import com.psyma17.healthyweightapplication.data.WeightData
import com.psyma17.healthyweightapplication.databinding.FragmentUserprofileBinding
import java.lang.StringBuilder

class UserProfileFragment : Fragment() {

    private lateinit var userProfileViewModel: UserProfileViewModel
    private var _binding: FragmentUserprofileBinding? = null
    lateinit var auth: FirebaseAuth
    private val userRef = Firebase.firestore.collection("users")

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userProfileViewModel =
            ViewModelProvider(this).get(UserProfileViewModel::class.java)

        _binding = FragmentUserprofileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        subscribeToRealTimeUpdates()

        binding.switchProfile.setOnCheckedChangeListener { _, onSwitch ->
            userRef.document(auth.currentUser?.uid.toString()).update(
                "meetFriend", onSwitch
            )
        }

        binding.buttonProfileEdit.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(super.getId() , UserProfileEditFragment())
                addToBackStack(null)
                commit()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeToRealTimeUpdates() {
        /*
        userRef.document(auth.currentUser?.uid.toString()).addSnapshotListener { value, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val userProfileData = it.toObject<UserProfileData>()
                if (userProfileData != null) {
                    setUpProfile(userProfileData)
                }
            }
        }
         */
        userRef.document(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userProfileData = document.toObject<UserProfileData>()
                    if (userProfileData != null) {
                        setUpProfile(userProfileData)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun setUpProfile(userProfileData: UserProfileData) {
        binding.textProfileName.text = userProfileData.userName
        binding.textProfileAboutMe2.text = userProfileData.aboutMe
        binding.textProfileCurrentWeight2.text = userProfileData.currentWeight.toString()
        binding.textProfileObjective2.text = userProfileData.objective
        binding.switchProfile.isChecked = userProfileData.meetFriend
    }
}