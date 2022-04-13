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
import java.text.SimpleDateFormat

class ChatAdapter (private var messageList : ArrayList<MessageData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var auth: FirebaseAuth

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

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentItem = messageList[position]
        val format = SimpleDateFormat("HH:mm dd.MM.yyyy ")
        when(holder.itemViewType) {
            0 -> {
                val chatSentViewHolder : ChatSentViewHolder = holder as ChatSentViewHolder
                holder.messageSent.text = currentItem.message
                holder.dateSent.text = "Sent: ${format.format(currentItem.dateSent)}"
            }
            else -> {
                val chatReceiveViewHolder : ChatReceiveViewHolder = holder as ChatReceiveViewHolder
                holder.messageReceived.text = currentItem.message
                holder.dateReceived.text = "Sent: ${format.format(currentItem.dateSent)}"
            }
        }
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

    fun getItemAt(position: Int) : MessageData {
        return messageList[position]
    }

    inner class ChatSentViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val messageSent : TextView = itemView.findViewById(R.id.textMessageSent)
        val dateSent : TextView = itemView.findViewById(R.id.textMessageSentDate)

    }

    inner class ChatReceiveViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val messageReceived : TextView = itemView.findViewById(R.id.textMessageReceived)
        val dateReceived : TextView = itemView.findViewById(R.id.textMessageReceivedDate)

    }
}