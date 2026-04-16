package com.example.personaleventplanner.ui.eventlist

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personaleventplanner.R
import com.example.personaleventplanner.data.local.AppDatabase
import com.example.personaleventplanner.data.local.entity.EventEntity
import com.example.personaleventplanner.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

class EventListFragment : Fragment(R.layout.fragment_event_list), EventClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: EventAdapter
    private lateinit var repository: EventRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewEvents)
        tvEmpty = view.findViewById(R.id.tvEmpty)

        val dao = AppDatabase.getDatabase(requireContext()).eventDao()
        repository = EventRepository(dao)

        adapter = EventAdapter(emptyList(), this)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        repository.allEvents.observe(viewLifecycleOwner) { events ->
            adapter.updateData(events)

            if (events.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onEditClick(event: EventEntity) {
        val bundle = Bundle().apply {
            putInt("id", event.id)
            putString("title", event.title)
            putString("category", event.category)
            putString("location", event.location)
            putLong("dateTime", event.dateTime)
        }

        findNavController().navigate(R.id.action_eventListFragment_to_editEventFragment, bundle)
    }

    override fun onDeleteClick(event: EventEntity) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            repository.delete(event)
            launch(Dispatchers.Main) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}