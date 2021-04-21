package c.m.koskosan.ui.form.order.dialog

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import c.m.koskosan.ui.form.order.OrderViewModel
import c.m.koskosan.util.Constants.Companion.FLAG_START_RENT_DATE
import c.m.koskosan.util.Constants.Companion.FLAG_STOP_RENT_DATE
import c.m.koskosan.util.Constants.Companion.FLAG_SURVEY_SCHEDULE_DATE
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*

class OrderDatePickerDialog : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var flag: Int = 0
    private val orderViewModel by sharedViewModel<OrderViewModel>()
    private var rentStartDate: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // get rent start date value for setup rent stop date
        orderViewModel.startRentDate.observe(this, { date ->
            if (date != null) {
                val stringLength = date.length
                rentStartDate = date.removeRange(2, stringLength).toInt()
            }
        })

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(
            requireActivity(),
            this,
            year,
            month,
            dayOfMonth
        ).apply {
            datePicker.minDate = calendar.time.time
        }
    }

    @SuppressLint("SimpleDateFormat", "WeekBasedYear")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val setCalendar = Calendar.getInstance()
        setCalendar.set(year, month, dayOfMonth)
        val format = SimpleDateFormat("d-MMMM-yyyy")
        val date = format.format(setCalendar.time)

        when (flag) {
            FLAG_SURVEY_SCHEDULE_DATE -> orderViewModel.selectedSurveyScheduleDate(date)
            FLAG_START_RENT_DATE -> orderViewModel.selectedStartRentDate(date)
            FLAG_STOP_RENT_DATE -> orderViewModel.selectedStopRentDate(date)
        }
    }
}