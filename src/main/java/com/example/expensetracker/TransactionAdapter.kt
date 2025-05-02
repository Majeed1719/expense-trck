package com.example.expensetracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    // ViewHolder class
    inner class TransactionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.label)
        val amount: TextView = view.findViewById(R.id.amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_layout, parent, false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.amount.context

        holder.amount.text = if (transaction.amount >= 0) {
            "+ ₹%.2f".format(transaction.amount)
        } else {
            "- ₹%.2f".format(Math.abs(transaction.amount))
        }

        // Set text color based on amount
        val colorRes = if (transaction.amount >= 0) R.color.green else R.color.red
        holder.amount.setTextColor(ContextCompat.getColor(context, colorRes))

        holder.label.text = transaction.label

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("transaction", transaction)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = transactions.size

    // Method to update the list dynamically
    fun setData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
