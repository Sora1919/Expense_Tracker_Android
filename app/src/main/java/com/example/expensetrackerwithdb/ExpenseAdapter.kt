package com.example.expensetrackerwithdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ExpenseAdapter(
    private val items: MutableList<Expense>,
    private val onClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseVH>() {

    private val nf = NumberFormat.getNumberInstance(Locale.US)

    class ExpenseVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseVH(v)
    }

    override fun onBindViewHolder(holder: ExpenseVH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvAmount.text = "${nf.format(item.amount)} Ks"
        holder.tvDate.text = item.date
        holder.tvCategory.text = item.category


        holder.itemView.setOnClickListener { onClick(position) }
    }

    override fun getItemCount(): Int = items.size
}
