package com.example.anandbhavan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anandbhavan.R
import com.example.anandbhavan.model.Policy
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Locale

class PolicyAdapter(
    private var policies: List<Policy>,
    private val onItemClick: (Policy) -> Unit
) : RecyclerView.Adapter<PolicyAdapter.PolicyViewHolder>() {

    class PolicyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvPolicyName)
        val tvNumber: TextView = itemView.findViewById(R.id.tvPolicyNumber)
        val tvDates: TextView = itemView.findViewById(R.id.tvDates)
        val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PolicyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_policy, parent, false)
        return PolicyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PolicyViewHolder, position: Int) {
        val policy = policies[position]
        holder.tvName.text = policy.name
        holder.tvNumber.text = "No: ${policy.policyNumber}"
        
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val expiry = policy.endDate?.let { dateFormat.format(it) } ?: "N/A"
        holder.tvDates.text = "Expires: $expiry"
        
        holder.chipStatus.text = policy.status
        
        holder.itemView.setOnClickListener { onItemClick(policy) }
    }

    override fun getItemCount() = policies.size
    
    fun updateList(newList: List<Policy>) {
        policies = newList
        notifyDataSetChanged()
    }
}
