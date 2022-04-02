package com.psyma17.healthyweightapplication.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.psyma17.healthyweightapplication.databinding.FragmentUserprofileBinding

class UserProfileFragment : Fragment() {

    private lateinit var userProfileViewModel: UserProfileViewModel
    private var _binding: FragmentUserprofileBinding? = null
    lateinit var auth: FirebaseAuth

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

        val textView: TextView = binding.textUserProfile
        userProfileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        auth = FirebaseAuth.getInstance()
        // binding.textDisplayName.text = auth.currentUser?.displayName.toString()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}