package com.psyma17.healthyweightapplication.ui.friend

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.psyma17.healthyweightapplication.Adapter.FriendsListAdapter
import com.psyma17.healthyweightapplication.R
import com.psyma17.healthyweightapplication.data.FriendData
import com.psyma17.healthyweightapplication.data.UserProfileData
import com.psyma17.healthyweightapplication.databinding.FragmentFriendBinding
import com.psyma17.healthyweightapplication.ui.friend.FriendFragment
import kotlinx.coroutines.*

class FriendFragment : Fragment() {

    private lateinit var viewModel: FriendViewModel
    private lateinit var friendsListAdapter: FriendsListAdapter
    private lateinit var friendsList: ArrayList<FriendData>
    private lateinit var userProfileList: ArrayList<UserProfileData>
    private var _binding: FragmentFriendBinding? = null
    private lateinit var auth: FirebaseAuth
    private val userRef = Firebase.firestore.collection("users")
    private lateinit var friendRef: CollectionReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(FriendViewModel::class.java)
        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = FirebaseAuth.getInstance()
        friendRef = Firebase.firestore.collection("friends/" + auth.currentUser?.uid + "/friendData")


        friendsList = ArrayList<FriendData>()
        friendsList.add(FriendData("sakjbfjabkj"))
        binding.friendRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.friendRecyclerView.setHasFixedSize(true)

        retrieveFriendData()

        friendsListAdapter = FriendsListAdapter(friendsList)
        binding.friendRecyclerView.adapter = friendsListAdapter

        setUpSearchView()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpSearchView() {
        binding.friendSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
    }

    private fun retrieveFriendData() = CoroutineScope(Dispatchers.IO).launch {
        val arrayList = arrayListOf<FriendData>()
        friendRef
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    arrayList.add(document.toObject<FriendData>())
                }
                friendsListAdapter.addNewItem(arrayList)
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

}