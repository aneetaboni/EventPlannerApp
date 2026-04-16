package com.example.personaleventplanner.ui.editevent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.personaleventplanner.R
import com.example.personaleventplanner.data.local.AppDatabase
import com.example.personaleventplanner.data.local.entity.EventEntity
import com.example.personaleventplanner.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditEventFragment : Fragment(R.layout.fragment_edit_event) {

    private lateinit var etTitle: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var etLocation: EditText
    private lateinit var btnSelectDate: Button
    private lateinit var btnSelectTime: Button
    private lateinit var tvSelectedDateTime: TextView
    private lateinit var btnUpdateEvent: Button

    private lateinit var repository: EventRepository
    private val calendar = Calendar.getInstance()

    private var eventId: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTitle = view.findViewById(R.id.etTitle)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        etLocation = view.findViewById(R.id.etLocation)
        btnSelectDate = view.findViewById(R.id.btnSelectDate)
        btnSelectTime = view.findViewById(R.id.btnSelectTime)
        tvSelectedDateTime = view.findViewById(R.id.tvSelectedDateTime)
        btnUpdateEvent = view.findViewById(R.id.btnUpdateEvent)

        val dao = AppDatabase.getDatabase(requireContext()).eventDao()
        repository = EventRepository(dao)

        setupCategorySpinner()
        setupDatePicker()
        setupTimePicker()
        loadExistingData()
        setupUpdateButton()
    }

    private fun setupCategorySpinner() {
        val categories = listOf("Work", "Social", "Travel")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun loadExistingData() {
        arguments?.let {
            eventId = it.getInt("id")
            etTitle.setText(it.getString("title"))
            etLocation.setText(it.getString("location"))

            val category = it.getString("category")
            val categories = listOf("Work", "Social", "Travel")
            spinnerCategory.setSelection(categories.indexOf(category))

            val dateTime = it.getLong("dateTime")
            calendar.timeInMillis = dateTime
            updateDateTimeText()
        }
    }

    private fun setupDatePicker() {
        btnSelectDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    calendar.set(Calendar.YEAR, y)
                    calendar.set(Calendar.MONTH, m)
                    calendar.set(Calendar.DAY_OF_MONTH, d)
                    updateDateTimeText()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTimePicker() {
        btnSelectTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, h, m ->
                    calendar.set(Calendar.HOUR_OF_DAY, h)
                    calendar.set(Calendar.MINUTE, m)
                    updateDateTimeText()
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }
    }

    private fun updateDateTimeText() {
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        tvSelectedDateTime.text = formatter.format(calendar.time)
    }

    private fun setupUpdateButton() {
        btnUpdateEvent.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()
            val location = etLocation.text.toString().trim()
            val dateTime = calendar.timeInMillis

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dateTime < System.currentTimeMillis()) {
                Toast.makeText(requireContext(), "Past date not allowed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedEvent = EventEntity(
                id = eventId,
                title = title,
                category = category,
                location = location,
                dateTime = dateTime
            )

            CoroutineScope(Dispatchers.IO).launch {
                repository.update(updatedEvent)

                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Event updated", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }
}