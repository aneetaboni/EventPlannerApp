package com.example.personaleventplanner.ui.eventlist

import com.example.personaleventplanner.data.local.entity.EventEntity

interface EventClickListener {
    fun onEditClick(event: EventEntity)
    fun onDeleteClick(event: EventEntity)
}