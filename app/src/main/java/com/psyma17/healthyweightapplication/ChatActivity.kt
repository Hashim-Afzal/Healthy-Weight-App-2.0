package com.psyma17.healthyweightapplication

import android.annotation.SuppressLint
import com.psyma17.healthyweightapplication.R
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.psyma17.healthyweightapplication.Adapter.ChatAdapter
import com.psyma17.healthyweightapplication.data.MessageData
import com.psyma17.healthyweightapplication.data.ReportData
import com.psyma17.healthyweightapplication.data.WeightData
import com.psyma17.healthyweightapplication.databinding.ActivityChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: ArrayList<MessageData>
    private lateinit var messageRef: CollectionReference
    private lateinit var uidFriend: String
    private lateinit var weightDataList: ArrayList<WeightData>

    companion object {
        const val UID_FRIEND_EXTRA = "UID_FRIEND_EXTRA"
        const val FRIEND_NAME = "FRIEND_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        weightDataList = ArrayList<WeightData>()

        val bundle :Bundle ?=intent.extras
        if (bundle!=null){
            uidFriend = bundle.getString(UID_FRIEND_EXTRA).toString()
            getWeightData(bundle.getString(UID_FRIEND_EXTRA).toString())
            val uidRefString: String = if (auth.currentUser?.uid.toString() < uidFriend.toString()) {
                "${auth.currentUser?.uid.toString()}/${uidFriend}"
            } else {
                "${uidFriend}/${auth.currentUser?.uid.toString()}"
            }
            messageRef = Firebase.firestore.collection("conversations/${uidRefString}/" )
            binding.chatTvFriendName.text = bundle.getString(FRIEND_NAME)
            //Toast.makeText(this, uidRefPath, Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }

        setUpRecyclerView()
        setUpButtons()
    }

    private fun setUpButtons() {
        setUpSendButton()
        setUpBackButton()
        setUpInfoButton()
        setUpReportButton()
    }

    private fun setUpBackButton() {
        binding.chatImageBack.setOnClickListener {
            finish()
        }
    }

    private fun setUpInfoButton() {
        binding.chatImageInfo.setOnClickListener {
            infoOnClickAlert()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun infoOnClickAlert() {
        /*
        val builder = AlertDialog.Builder(applicationContext)



        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(getString(R.string.close)) { dialog, which ->
            // Toast.makeText(applicationContext, "yes", Toast.LENGTH_SHORT).show()
        }
        builder.show()
         */

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.weightInfo))

        val sb: StringBuilder = StringBuilder()
        val format = SimpleDateFormat("dd.MM.yyyy ")
        for (weightData in weightDataList) {
            sb.append("Weight ${weightData.weight} at ${format.format(weightData.time)}\n")
        }
        builder.setMessage(sb.toString())
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(R.string.close) { dialog, which ->
            // Toast.makeText(applicationContext, "yes", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun getWeightData(uidFriend: String) = CoroutineScope(Dispatchers.IO).launch {
        Firebase.firestore.collection("weight/" + uidFriend + "/weightData")
            .orderBy("time", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    val weightData = document.toObject<WeightData>()
                    if (weightData != null) {
                        weightDataList.add(weightData)
                        Log.d("Tag", "$weightData")
                    }
                }
            }
            .addOnFailureListener {
                Log.d("Tag", "Failed to retrieve data")
            }
    }

    private fun setUpReportButton() {
        binding.chatImageReport.setOnClickListener {
            reportOnClickAlert()
        }
    }

    private fun reportOnClickAlert() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.reportAbuse))
        builder.setMessage(getString(R.string.reasonReport))
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        val spinner: Spinner = Spinner(this)
        val arrayAdapter: SpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.reportReasons,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = arrayAdapter
        builder.setView(spinner)

        builder.setPositiveButton("Report") { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                Firebase.firestore.collection("reports/${uidFriend}/reportData")
                    .document(auth.currentUser?.uid.toString()).set(
                        ReportData(
                            uidOfReported = uidFriend,
                            uidOfReporter = auth.currentUser?.uid.toString(),
                            reportReason = spinner.selectedItem.toString()
                        )
                    )
                    .addOnSuccessListener {
                            Toast.makeText(this@ChatActivity,"User successfully reported", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        builder.show()
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
            binding.chatEtMessage.text.clear()
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

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