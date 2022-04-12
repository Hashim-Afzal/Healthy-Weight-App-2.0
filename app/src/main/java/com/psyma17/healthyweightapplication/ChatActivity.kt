package com.psyma17.healthyweightapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.psyma17.healthyweightapplication.Adapter.ChatAdapter
import com.psyma17.healthyweightapplication.Adapter.FriendsListAdapter
import com.psyma17.healthyweightapplication.data.MessageData
import com.psyma17.healthyweightapplication.data.UserProfileData
import com.psyma17.healthyweightapplication.data.WeightData
import com.psyma17.healthyweightapplication.databinding.ActivityChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: ArrayList<MessageData>
    private lateinit var messageRef: CollectionReference
    // private lateinit var

    companion object {
        const val UID_FRIEND_EXTRA = "UID_FRIEND_EXTRA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        val bundle :Bundle ?=intent.extras
        if (bundle!=null){
            val uidFriend = bundle.getString(UID_FRIEND_EXTRA)
            messageRef = Firebase.firestore.collection("conversations/${auth.currentUser?.uid.toString()}/${uidFriend}/" )
            //Toast.makeText(this, uidRefPath, Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }

        setUpRecyclerView()
        setUpButtons()
    }

    private fun setUpButtons() {
        setUpSendButton()
    }

    private fun setUpSendButton() {
        binding.chatSend.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val message = binding.chatEtMessage.text.toString()
                if (message.isNotEmpty()) {
                    try {
                        val messageData = MessageData(
                            dateSent = System.currentTimeMillis(),
                            message = message,
                            uidSender = (auth.currentUser?.uid.toString())
                        )
                        messageRef.add(messageData).await()
                    } catch (e: Exception) {
                        Log.d("TAG", "Error: " + e.message)
                    }
                }
            }
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
        binding.chatEtMessage.text.clear()
    }
    private fun setUpRecyclerView() {
        messageList = ArrayList<MessageData>()
        subscribeToRealTimeUpdates()
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.setHasFixedSize(true)
        chatAdapter = ChatAdapter(messageList)
        binding.chatRecyclerView.adapter = chatAdapter
    }

    private fun subscribeToRealTimeUpdates() {
        messageRef.addSnapshotListener { value, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val messageArray = ArrayList<MessageData>()
                for (document in it.documents) {
                    document.toObject<MessageData>()?.let { it1 -> messageArray.add(it1) }
                }
                messageArray.sortBy { it.dateSent }
                chatAdapter.addMessageList(messageArray)
                binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount -1)
            }
        }
    }
}