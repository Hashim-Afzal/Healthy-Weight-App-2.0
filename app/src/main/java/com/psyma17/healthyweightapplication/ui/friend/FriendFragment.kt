package com.psyma17.healthyweightapplication.ui.friend

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.psyma17.healthyweightapplication.Adapter.FriendsListAdapter
import com.psyma17.healthyweightapplication.R
import com.psyma17.healthyweightapplication.data.FriendData
import com.psyma17.healthyweightapplication.databinding.FragmentFriendBinding
import com.psyma17.healthyweightapplication.ui.friend.FriendFragment

class FriendFragment : Fragment() {

    companion object {
        fun newInstance() = FriendFragment()
    }

    private lateinit var viewModel: FriendViewModel
    private lateinit var friendsListRecyclerView: RecyclerView
    private lateinit var friendsList: ArrayList<FriendData>
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

        setUpRecyclerView()
        setUpSearchView()

        //val textView: TextView = binding.textFriend
        viewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpSearchView() {
        binding.friendSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setUpRecyclerView() {
        friendsListRecyclerView = binding.friendRecyclerView
        friendsListRecyclerView.layoutManager = LinearLayoutManager(context)
        friendsListRecyclerView.setHasFixedSize(true)

        friendsList = arrayListOf<FriendData>()

        friendsList.add(FriendData("dshfksdnf"))
        friendsList.add(FriendData("dshfksadasdsdnf"))
        friendsList.add(FriendData("dshf223ksdnf"))
        friendsList.add(FriendData("dshf223ksdnf"))
        friendsList.add(FriendData("dshf223ksdnf"))
        friendsList.add(FriendData("dshf223ksdnf"))
        friendsList.add(FriendData("dshf223ksdnf"))


        friendsListRecyclerView.adapter = FriendsListAdapter(friendsList)
    }
}