package com.example.mycalc2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycalc2.R

class HistoryAdapter(private val historyList: MutableList<Pair<String, String>>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvExpression: TextView = view.findViewById(R.id.tvExpression)
        val tvResult: TextView = view.findViewById(R.id.tvResultHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val (expression, result) = historyList[position]
        holder.tvExpression.text = expression
        holder.tvResult.text = result
    }

    override fun getItemCount(): Int = historyList.size
}
