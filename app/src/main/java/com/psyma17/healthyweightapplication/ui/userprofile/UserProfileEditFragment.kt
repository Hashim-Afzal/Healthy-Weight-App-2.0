package com.psyma17.healthyweightapplication.ui.userprofile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.psyma17.healthyweightapplication.R
import com.psyma17.healthyweightapplication.data.UserProfileData
import com.psyma17.healthyweightapplication.databinding.FragmentUserProfileEditBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 * Use the [UserProfileEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserProfileEditFragment : Fragment() {

    private var _binding: FragmentUserProfileEditBinding? = null
    private val binding get() = _binding!!
    lateinit var auth: FirebaseAuth
    private val userRef = Firebase.firestore.collection("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentUserProfileEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        retrieveUserProfileData()

        binding.buttonEditProfileAccept.setOnClickListener {
            updateUserProfileData()
            parentFragmentManager.popBackStack()
        }

        binding.buttonEditProfileCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return root
    }

    private fun retrieveUserProfileData() = CoroutineScope(Dispatchers.IO).launch {
        userRef.document(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userProfileData = document.toObject<UserProfileData>()
                    if (userProfileData != null) {
                        setUpEditProfile(userProfileData)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun updateUserProfileData() {
        val userRefDocument = userRef.document(auth.currentUser?.uid.toString())
        val updates = hashMapOf<String, Any>(
            "userName" to binding.etEditProfileDisplayName.text.toString(),
            "aboutMe" to binding.etEditProfileAboutMe.text.toString(),
            "objective" to binding.spinnerEditProfileObjective.selectedItem.toString()
        )

        userRefDocument.update(updates).addOnCompleteListener { }
    }

    private fun setUpEditProfile(userProfileData: UserProfileData) {
        ArrayAdapter.createFromResource(
            context!!,
            R.array.objectives,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinnerEditProfileObjective.adapter = adapter
        }

        binding.etEditProfileAboutMe.setText(userProfileData.aboutMe)
        binding.etEditProfileDisplayName.setText(userProfileData.userName)

        when(userProfileData.objective) {
            "gain" -> {binding.spinnerEditProfileObjective.setSelection(0)}
            "lose" -> {binding.spinnerEditProfileObjective.setSelection(1)}
            else -> {binding.spinnerEditProfileObjective.setSelection(2)}
        }
    }
}