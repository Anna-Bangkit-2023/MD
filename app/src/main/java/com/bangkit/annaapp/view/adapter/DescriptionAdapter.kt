package com.bangkit.annaapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.annaapp.R
import com.bangkit.annaapp.data.remote.response.DefinitionsItem

class DescriptionAdapter(private val descriptions: List<DefinitionsItem>) :
    RecyclerView.Adapter<DescriptionAdapter.DescriptionViewHolder>() {

    class DescriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_description, parent, false)
        return DescriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {
        holder.tvDescription.text = descriptions[position].definition
    }

    override fun getItemCount(): Int = descriptions.size
}