package com.example.basmaalqethamiexam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_row.view.*

class RowRecyclerView (private val answers: ArrayList<String>): RecyclerView.Adapter<RowRecyclerView.ItemViewHolder>() {
    class ItemViewHolder (itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val answer = answers[position]
        holder.itemView.apply {
            textViewRowItem.text = answer
        }
    }

    override fun getItemCount() = answers.size

}