package com.example.projekconsultant.adapter

import KeunggulanHome
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projekconsultant.R
import com.google.android.flexbox.FlexboxLayout

class KeunggulanHomeAdapter(private val desc: List<KeunggulanHome>) : RecyclerView.Adapter<KeunggulanHomeAdapter.KeunggulanViewHolder>() {
    inner class KeunggulanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val desc:TextView = itemView.findViewById(R.id.Desccs)
        val numbe:TextView = itemView.findViewById(R.id.number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeunggulanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_keunggulan_home, parent, false)


        return KeunggulanViewHolder(view)
    }

    override fun onBindViewHolder(holder: KeunggulanViewHolder, position: Int) {
        val descrip = desc[position].descrip
        val numbs = position + 1

        holder.desc.setText("${descrip}")
        holder.numbe.setText("${numbs}")

    }

    override fun getItemCount(): Int {
        return  desc.size
    }
}