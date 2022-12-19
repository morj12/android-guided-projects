package com.example.top.activity

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import java.util.*

class DialogSelectorDate: DialogFragment() {

    companion object {
        const val DATE = "date"
        const val SELECTED_DATE = "selectedDate"
    }

    lateinit var listener: DatePickerDialog.OnDateSetListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance(Locale.ROOT)
        val date = arguments?.getLong(DATE)
        if (date != null) {
            calendar.timeInMillis = date
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(activity, listener, year, month, day)
    }
}
