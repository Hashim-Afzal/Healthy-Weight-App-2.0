package com.psyma17.healthyweightapplication.ui.friend

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.psyma17.healthyweightapplication.Adapter.FriendsListAdapter
import com.psyma17.healthyweightapplication.ChatActivity
import com.psyma17.healthyweightapplication.data.FriendData
import com.psyma17.healthyweightapplication.data.UserProfileData
import com.psyma17.healthyweightapplication.databinding.FragmentFriendBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FriendFragment : Fragment() {

    private lateinit var viewModel: FriendViewModel
    private lateinit var friendsListAdapter: FriendsListAdapter
    private lateinit var userProfileList: ArrayList<UserProfileData>
    private lateinit var userProfileListSearch: ArrayList<UserProfileData>
    private var _binding: FragmentFriendBinding? = null
    private lateinit var auth: FirebaseAuth
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
        friendRef = Firebase.firestore.collection("friends/" + auth.currentUser?.uid.toString() + "/friendData")

        setUpRecyclerView()
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
                Log.d("TAG", "Entering search view")
                userProfileListSearch = ArrayList<UserProfileData>()
                Firebase.firestore.collection("users")
                    .whereEqualTo("meetFriend", true)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if (document.toObject<UserProfileData>().userName.contains(p0.toString(), ignoreCase = true)
                                && document.toObject<UserProfileData>()?.uid != auth.currentUser?.uid) {
                                userProfileListSearch.add(document.toObject<UserProfileData>())
                            }
                            Log.d("TAG setupSearchView", "${document.id} => ${document.data}")
                        }
                        friendsListAdapter.setNewItems(userProfileListSearch)
                        userProfileListSearch.clear()
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "get failed search with ", exception)
                    }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })
    }

    private fun setUpRecyclerView() {
        userProfileList = ArrayList<UserProfileData>()
        binding.friendRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.friendRecyclerView.setHasFixedSize(true)
        friendsListAdapter = FriendsListAdapter(userProfileList)
        binding.friendRecyclerView.adapter = friendsListAdapter
        friendsListAdapter.onItemClick = {
            Log.d("TAG", "User ID " + it.uid)
            friendRef.document(it.uid).set(FriendData(uid = it.uid))
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra(ChatActivity.UID_FRIEND_EXTRA, it.uid)
            intent.putExtra(ChatActivity.FRIEND_NAME, it.userName)
            startActivity(intent)
        }
        addFriendsToRecyclerView()
    }

    private fun addFriendsToRecyclerView() = CoroutineScope(Dispatchers.IO).launch {
        val arrayListFriendData = arrayListOf<FriendData>()
        friendRef
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    if (document.toObject<FriendData>().uid != auth.currentUser?.uid) {
                        arrayListFriendData.add(document.toObject<FriendData>())
                    }
                }
               CoroutineScope(Dispatchers.IO).launch {
                   val arrayListUserProfileData = getUserProfileDataListFromFriendData(arrayListFriendData)
                   withContext(Dispatchers.Main) {
                       friendsListAdapter.setNewItems(arrayListUserProfileData)
                   }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private suspend fun getUserProfileDataListFromFriendData(friendDataList: ArrayList<FriendData>): ArrayList<UserProfileData> {
        val userProfilesData = mutableListOf<Deferred<UserProfileData>>()
        coroutineScope {
            for (friendData in friendDataList) {
                val userProfileData = async {
                    getUserProfileData(friendData.uid)
                }
                userProfilesData.add(userProfileData)
            }
        }
        return ArrayList(userProfilesData.awaitAll())
    }

    private suspend fun getUserProfileData(uidFriend: String): UserProfileData {
        return try {
            val document = Firebase.firestore.collection("users").document(uidFriend)
                .get()
                .await()
            if (document != null && document.toObject<UserProfileData>()?.uid != auth.currentUser?.uid) {
                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                document.toObject<UserProfileData>()!!
            } else {
                Log.d("TAG", "No such document")
                UserProfileData()
            }
        } catch (e : Exception) {
            Log.d("TAG", "get failed with ", e)
            UserProfileData()
        }
    }
}