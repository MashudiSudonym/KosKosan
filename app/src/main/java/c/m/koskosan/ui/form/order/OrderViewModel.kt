package c.m.koskosan.ui.form.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel() : ViewModel() {
    // survey schedule date
    private val mutableSurveyScheduleDate = MutableLiveData<String>()
    val surveyScheduleDate: LiveData<String> get() = mutableSurveyScheduleDate

    fun selectedSurveyScheduleDate(date: String) {
        mutableSurveyScheduleDate.value = date
    }

    // start rent date
    private val mutableStartRentDate = MutableLiveData<String>()
    val startRentDate: LiveData<String> get() = mutableStartRentDate

    fun selectedStartRentDate(date: String) {
        mutableStartRentDate.value = date
    }

    // stop rent date
    private val mutableStopRentDate = MutableLiveData<String>()
    val stopRentDate: LiveData<String> get() = mutableStopRentDate

    fun selectedStopRentDate(date: String) {
        mutableStopRentDate.value = date
    }
}