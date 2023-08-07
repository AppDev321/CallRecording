package com.example.callrecording.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.callrecording.database.tables.PhoneCall
import com.example.callrecording.databinding.ItemCallBinding

class CallHistoryAdapter(
    private val phoneCall: List<PhoneCall>
) : RecyclerView.Adapter<CallHistoryViewHolder>() {

    private lateinit var binding: ItemCallBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallHistoryViewHolder {
        binding = ItemCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CallHistoryViewHolder, position: Int) {
        val largeNews = phoneCall[position]
        holder.bind(largeNews)
    }

    override fun getItemCount(): Int = phoneCall.size

}

class CallHistoryViewHolder(
    private val binding: ItemCallBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(callData: PhoneCall) {
        binding.phoneCallRecrod = callData
    }
}
