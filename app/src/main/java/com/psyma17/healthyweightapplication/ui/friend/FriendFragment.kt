package com.psyma17.healthyweightapplication.ui.friend

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.psyma17.healthyweightapplication.R
import com.psyma17.healthyweightapplication.databinding.FragmentFriendBinding
import com.psyma17.healthyweightapplication.ui.friend.FriendFragment

class FriendFragment : Fragment() {

    companion object {
        fun newInstance() = FriendFragment()
    }

    private lateinit var viewModel: FriendViewModel
    private var _binding: FragmentFriendBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(FriendViewModel::class.java)

        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textFriend
        viewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}