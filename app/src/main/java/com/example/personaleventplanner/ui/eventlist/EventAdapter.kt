package com.example.personaleventplanner.ui.eventlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personaleventplanner.R
import com.example.personaleventplanner.data.local.entity.EventEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventAdapter(
    private var eventList: List<EventEntity>,
    private val listener: EventClickListener
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]

        holder.tvTitle.text = event.title
        holder.tvCategory.text = "Category: ${event.category}"
        holder.tvLocation.text = "Location: ${event.location}"

        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        holder.tvDateTime.text = "Date: ${formatter.format(Date(event.dateTime))}"

        holder.btnEdit.setOnClickListener {
            listener.onEditClick(event)
        }

        holder.btnDelete.setOnClickListener {
            listener.onDeleteClick(event)
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    fun updateData(newList: List<EventEntity>) {
        eventList = newList
        notifyDataSetChanged()
    }

}