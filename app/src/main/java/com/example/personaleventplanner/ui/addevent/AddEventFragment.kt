package com.example.personaleventplanner.ui.addevent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.personaleventplanner.R
import com.example.personaleventplanner.data.local.AppDatabase
import com.example.personaleventplanner.data.local.entity.EventEntity
import com.example.personaleventplanner.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEventFragment : Fragment(R.layout.fragment_add_event) {

    private lateinit var etTitle: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var etLocation: EditText
    private lateinit var btnSelectDate: Button
    private lateinit var btnSelectTime: Button
    private lateinit var tvSelectedDateTime: TextView
    private lateinit var btnSaveEvent: Button

    private lateinit var repository: EventRepository
    private val calendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTitle = view.findViewById(R.id.etTitle)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        etLocation = view.findViewById(R.id.etLocation)
        btnSelectDate = view.findViewById(R.id.btnSelectDate)
        btnSelectTime = view.findViewById(R.id.btnSelectTime)
        tvSelectedDateTime = view.findViewById(R.id.tvSelectedDateTime)
        btnSaveEvent = view.findViewById(R.id.btnSaveEvent)

        val dao = AppDatabase.getDatabase(requireContext()).eventDao()
        repository = EventRepository(dao)

        setupCategorySpinner()
        setupDatePicker()
        setupTimePicker()
        setupSaveButton()
    }

    private fun setupCategorySpinner() {
        val categories = listOf("Work", "Social", "Travel")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun setupDatePicker() {
        btnSelectDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                updateDateTimeText()
            }, year, month, day).show()
        }
    }

    private fun setupTimePicker() {
        btnSelectTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                calendar.set(Calendar.SECOND, 0)
                updateDateTimeText()
            }, hour, minute, false).show()
        }
    }

    private fun updateDateTimeText() {
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        tvSelectedDateTime.text = formatter.format(calendar.time)
    }

    private fun setupSaveButton() {
        btnSaveEvent.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()
            val location = etLocation.text.toString().trim()
            val selectedTime = calendar.timeInMillis

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tvSelectedDateTime.text.toString() == "No date and time selected") {
                Toast.makeText(requireContext(), "Please select date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedTime < System.currentTimeMillis()) {
                Toast.makeText(requireContext(), "Past date is not allowed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val event = EventEntity(
                title = title,
                category = category,
                location = location,
                dateTime = selectedTime
            )

            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(event)

                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Event saved successfully", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            }
        }
    }

    private fun clearFields() {
        etTitle.text.clear()
        etLocation.text.clear()
        spinnerCategory.setSelection(0)
        tvSelectedDateTime.text = "No date and time selected"
        calendar.timeInMillis = System.currentTimeMillis()
    }
}