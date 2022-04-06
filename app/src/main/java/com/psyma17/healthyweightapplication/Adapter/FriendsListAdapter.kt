package com.psyma17.healthyweightapplication.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.psyma17.healthyweightapplication.R
import com.psyma17.healthyweightapplication.data.FriendData
import com.psyma17.healthyweightapplication.data.UserProfileData

class FriendsListAdapter(private var userList : ArrayList<FriendData>) : RecyclerView.Adapter<FriendsListAdapter.FriendsListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsListViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_friend_item,
        parent, false)

        return FriendsListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendsListViewHolder, position: Int) {

        val currentItem = userList[position]
        // Change after implementing user images
        holder.friendsListImage.setImageResource(R.drawable.ic_baseline_person_24)
        holder.tvFriendName.text = currentItem.uid
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewItem(itemsNew: ArrayList<FriendData>){
        userList.addAll(itemsNew)
        notifyDataSetChanged()
    }

    class FriendsListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val friendsListImage : ShapeableImageView = itemView.findViewById(R.id.friend_list_image)
        val tvFriendName : TextView = itemView.findViewById(R.id.friend_list_name)


    }
}