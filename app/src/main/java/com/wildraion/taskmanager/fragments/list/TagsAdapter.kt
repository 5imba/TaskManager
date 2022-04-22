package com.wildraion.taskmanager.fragments.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wildraion.taskmanager.R

class TagsAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>(){

    private var tagsList = listOf<String>()


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.MyViewHolder {
        return ListAdapter.MyViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.tag_item, parent, false))
    }

    override fun onBindViewHolder(holder: ListAdapter.MyViewHolder, position: Int) {
        val currentItem = tagsList[position]
        holder.itemView.findViewById<TextView>(R.id.tag_text).text = currentItem.trim()
    }

    override fun getItemCount(): Int {
        return tagsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(tagsList: List<String>, ){
        this.tagsList = tagsList
        notifyDataSetChanged()
    }
}