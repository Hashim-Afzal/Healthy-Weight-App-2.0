package com.psyma17.healthyweightapplication.ui.weight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.psyma17.healthyweightapplication.data.WeightData
import com.psyma17.healthyweightapplication.databinding.FragmentWeightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class WeightFragment : Fragment() {

    private lateinit var weightViewModel: WeightViewModel
    private lateinit var weightDataCollectionRef: CollectionReference
    private var _binding: FragmentWeightBinding? = null
    lateinit var auth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        weightViewModel =
            ViewModelProvider(this).get(WeightViewModel::class.java)

        _binding = FragmentWeightBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        weightViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        auth = FirebaseAuth.getInstance()
        weightDataCollectionRef = Firebase.firestore.collection("weight/" + auth.currentUser?.uid + "/weightData")
        subscribeToRealTimeUpdates()
        binding.btnWeightSubmit.setOnClickListener {
            btnSaveWeightData()
        }
        binding.btnWeightRemoveLast.setOnClickListener {
            btnRemoveLastWeightData()
        }

        return root
    }

    private fun subscribeToRealTimeUpdates() {
        weightDataCollectionRef.addSnapshotListener { value, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            value?.let {
                val stringBuilder = StringBuilder()
                for (document in it.documents) {
                    val weightData = document.toObject<WeightData>()
                    stringBuilder.append("$weightData\n")
                }
                binding.textViewWeight.text = stringBuilder.toString()
            }
        }
    }

    private fun saveWeightData(weightData: WeightData) = CoroutineScope(Dispatchers.IO).launch {
        try {
            weightDataCollectionRef.add(weightData).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Successfully saved data.", Toast.LENGTH_LONG).show()
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun btnSaveWeightData() {
        val temp = binding.editTextNumberDecimal.text.toString()
        if (temp.isEmpty()) {
            return
        }

        val currentWeight = temp.toDouble()
        /*
        if (currentWeight < 10 || currentWeight > 300) {
            return
        }
         */
        val currentTime = System.currentTimeMillis()
        val weightData = WeightData(currentWeight, currentTime)
        saveWeightData(weightData)
    }

    private fun btnRemoveLastWeightData() = CoroutineScope(Dispatchers.IO).launch {
        val querySnapshot = weightDataCollectionRef.orderBy("time", Query.Direction.DESCENDING).limit(1).get().await()

        if (querySnapshot.documents.isNotEmpty()) {
            try {
                weightDataCollectionRef.document(querySnapshot.documents[0].id).delete().await()
            } catch (e: java.lang.Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}