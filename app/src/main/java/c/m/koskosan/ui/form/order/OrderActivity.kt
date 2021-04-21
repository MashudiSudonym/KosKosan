package c.m.koskosan.ui.form.order

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import c.m.koskosan.R
import c.m.koskosan.databinding.ActivityOrderBinding
import c.m.koskosan.util.Constants.Companion.FLAG_START_RENT_DATE
import c.m.koskosan.util.Constants.Companion.FLAG_STOP_RENT_DATE
import c.m.koskosan.util.Constants.Companion.FLAG_SURVEY_SCHEDULE_DATE
import c.m.koskosan.ui.form.order.dialog.OrderBottomSheetDialog
import c.m.koskosan.util.Constants.Companion.LOCATION_ADDRESS
import c.m.koskosan.util.Constants.Companion.LOCATION_NAME
import c.m.koskosan.util.Constants.Companion.LOCATION_PHONE
import c.m.koskosan.util.Constants.Companion.UID
import c.m.koskosan.ui.form.order.dialog.OrderDatePickerDialog
import id.rizmaulana.sheenvalidator.lib.SheenValidator
import org.koin.androidx.viewmodel.ext.android.viewModel


class OrderActivity : AppCompatActivity() {
    private lateinit var orderBinding: ActivityOrderBinding
    private val orderViewModel: OrderViewModel by viewModel()
    private lateinit var layout: View
    private lateinit var sheenValidator: SheenValidator
    private var locationUID: String? = ""
    private var locationName: String? = ""
    private var locationAddress: String? = ""
    private var locationPhone: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize view binding
        orderBinding = ActivityOrderBinding.inflate(layoutInflater)
        val view = orderBinding.root
        setContentView(view)

        // AppBar / ActionBar Title Setup
        setSupportActionBar(orderBinding.toolbarOrder)
        supportActionBar?.apply {
            title = getString(R.string.order)
            setDisplayHomeAsUpEnabled(true)
        }

        // initialize layout variable for help using the layout utilities
        layout = view

        // get parsing intent data
        locationUID = intent.getStringExtra(UID)
        locationName = intent.getStringExtra(LOCATION_NAME)
        locationAddress = intent.getStringExtra(LOCATION_ADDRESS)
        locationPhone = intent.getStringExtra(LOCATION_PHONE)

        // set location name text view
        orderBinding.tvNameLocationOrder.text = locationName

        // share parsing intent data with bottomsheet dialog
        orderViewModel.setLocationUID(locationUID.toString())
        orderViewModel.setLocationName(locationName.toString())
        orderViewModel.setLocationAddress(locationAddress.toString())
        orderViewModel.setLocationPhone(locationPhone.toString())

        // selectedDate for survey schedule date, start rent date, stop rent date
        selectedDate()

        // validate edit text have a value
        fieldValidation()

    }

    // validate edit text have a value
    private fun fieldValidation() {
        sheenValidator = SheenValidator(this).also { sheenValidator ->
            sheenValidator.registerAsRequired(orderBinding.edtSurveyScheduleDate)
            sheenValidator.registerAsRequired(orderBinding.edtRentStartDate)
            sheenValidator.registerAsRequired(orderBinding.edtRentStopDate)
            sheenValidator.setOnValidatorListener {
                // show confirmation order button sheet dialog
                confirmationOrderButtonSheet()
            }
        }

        // order button
        orderBinding.btnOrderOrder.setOnClickListener {
            sheenValidator.validate()
        }
    }

    // confirmation order button sheet dialog
    private fun confirmationOrderButtonSheet() {
        val orderConfirmationBottomSheetDialog = OrderBottomSheetDialog()
        orderConfirmationBottomSheetDialog.show(
            supportFragmentManager,
            getString(R.string.order_confirmation)
        )

    }

    // selectedDate for survey schedule date, start rent date, stop rent date
    private fun selectedDate() {
        // Initialize date picker fragment
        val orderDatePickerDialog = OrderDatePickerDialog()

        // selected survey schedule date
        orderBinding.edtSurveyScheduleDate.setOnClickListener {
            orderDatePickerDialog.flag = FLAG_SURVEY_SCHEDULE_DATE
            orderDatePickerDialog.show(
                supportFragmentManager,
                getString(R.string.survey_schedule_date)
            )
        }

        // selected start rent date
        orderBinding.edtRentStartDate.setOnClickListener {
            orderDatePickerDialog.flag = FLAG_START_RENT_DATE
            orderDatePickerDialog.show(supportFragmentManager, getString(R.string.rent_start_date))

            // if edit text rent start date is edited, edit text rent is disable
            orderBinding.edtRentStopDate.setText("")
        }


        // if edit text rent start date is empty, edit text rent stop date is disable
        orderBinding.edtRentStartDate.doAfterTextChanged { orderBinding.edtRentStopDate.isEnabled = true }

        // selected stop rent date
        orderBinding.edtRentStopDate.setOnClickListener {
            orderDatePickerDialog.flag = FLAG_STOP_RENT_DATE
            orderDatePickerDialog.show(supportFragmentManager, getString(R.string.rent_stop_date))
        }

        // observe value date for survey schedule, rent start, rent stop
        orderViewModel.surveyScheduleDate.observe(this, { result ->
            orderBinding.edtSurveyScheduleDate.setText(result)
        })

        orderViewModel.startRentDate.observe(this, { result ->
            orderBinding.edtRentStartDate.setText(result)
        })

        orderViewModel.stopRentDate.observe(this, { result ->
            orderBinding.edtRentStopDate.setText(result)
        })
    }

    // function for app bar back arrow
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}