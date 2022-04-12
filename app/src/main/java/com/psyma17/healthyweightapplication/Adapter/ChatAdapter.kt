package com.psyma17.healthyweightapplication.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.psyma17.healthyweightapplication.R
import com.psyma17.healthyweightapplication.data.MessageData

class ChatAdapter (private var messageList : ArrayList<MessageData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var auth: FirebaseAuth
    var onItemClick: ((MessageData) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        auth = FirebaseAuth.getInstance()
        return if (messageList[position].uidSender == auth.currentUser?.uid.toString()) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        auth = FirebaseAuth.getInstance()
        return when(viewType) {
            0 -> {
                val itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.list_chat_sent_item,
                    parent, false)
                ChatSentViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.list_chat_received_item,
                    parent, false)
                ChatReceiveViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentItem = messageList[position]
        when(holder.itemViewType) {
            0 -> {
                val chatSentViewHolder : ChatSentViewHolder = holder as ChatSentViewHolder
                holder.messageSent.text = currentItem.message
                holder.dateSent.text = currentItem.dateSent.toString()
            }
            else -> {
                val chatSentViewHolder : ChatReceiveViewHolder = holder as ChatReceiveViewHolder
                holder.messageReceived.text = currentItem.message
                holder.dateReceived.text = currentItem.dateSent.toString()
            }
        }
        // Change after implementing user images
        // holder.friendsListImage.setImageResource(R.drawable.ic_baseline_person_24)
        // holder.tvFriendName.text = currentItem.userName
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewMessage(itemsNew: MessageData){
        messageList.add(itemsNew)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addMessageList(itemsNew: ArrayList<MessageData>){
        messageList.clear()
        messageList.addAll(itemsNew)
        notifyDataSetChanged()

    }

    inner class ChatSentViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val messageSent : TextView = itemView.findViewById(R.id.textMessageSent)
        val dateSent : TextView = itemView.findViewById(R.id.textMessageSentDate)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(messageList[adapterPosition])
            }
        }
    }

    inner class ChatReceiveViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val messageReceived : TextView = itemView.findViewById(R.id.textMessageReceived)
        val dateReceived : TextView = itemView.findViewById(R.id.textMessageReceivedDate)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(messageList[adapterPosition])
            }
        }
    }

}