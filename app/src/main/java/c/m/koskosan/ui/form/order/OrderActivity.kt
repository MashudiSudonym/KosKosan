package c.m.koskosan.ui.form.order

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import c.m.koskosan.R
import c.m.koskosan.databinding.ActivityOrderBinding
import c.m.koskosan.databinding.BottomSheetOrderConfirmationBinding
import c.m.koskosan.util.Constants.Companion.FLAG_START_RENT_DATE
import c.m.koskosan.util.Constants.Companion.FLAG_STOP_RENT_DATE
import c.m.koskosan.util.Constants.Companion.FLAG_SURVEY_SCHEDULE_DATE
import c.m.koskosan.util.dialog.DatePickerFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.rizmaulana.sheenvalidator.lib.SheenValidator
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderActivity : AppCompatActivity() {
    private lateinit var orderBinding: ActivityOrderBinding
    private val orderViewModel: OrderViewModel by viewModel()
    private lateinit var layout: View
    private lateinit var sheenValidator: SheenValidator
    private lateinit var bottomSheet: View
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private var sheetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize view binding
        orderBinding = ActivityOrderBinding.inflate(layoutInflater)
        val view = orderBinding.root
        setContentView(view)

        // initialize layout variable for help using the layout utilities
        layout = view

        // Initialize bottom sheet
        bottomSheetInitialize()

        // selectedDate for survey schedule date, start rent date, stop rent date
        selectedDate()

        // validate edit text have a value
        sheenValidator = SheenValidator(this).also { sheenValidator ->
            sheenValidator.registerAsRequired(orderBinding.edtSurveyScheduleDate)
            sheenValidator.registerAsRequired(orderBinding.edtRentStartDate)
            sheenValidator.registerAsRequired(orderBinding.edtRentStopDate)
            sheenValidator.setOnValidatorListener {
                orderConfirmationBottomSheet()
            }
        }

        // order button
        orderBinding.btnOrderOrder.setOnClickListener {
            sheenValidator.validate()
        }
    }

    // selectedDate for survey schedule date, start rent date, stop rent date
    private fun selectedDate() {
        // Initialize date picker fragment
        val datePickerFragment = DatePickerFragment()

        // selected survey schedule date
        orderBinding.edtSurveyScheduleDate.setOnClickListener {
            datePickerFragment.flag = FLAG_SURVEY_SCHEDULE_DATE
            datePickerFragment.show(
                supportFragmentManager,
                getString(R.string.survey_schedule_date)
            )
        }

        // selected start rent date
        orderBinding.edtRentStartDate.setOnClickListener {
            datePickerFragment.flag = FLAG_START_RENT_DATE
            datePickerFragment.show(supportFragmentManager, getString(R.string.rent_start_date))
        }

        // selected stop rent date
        orderBinding.edtRentStopDate.setOnClickListener {
            datePickerFragment.flag = FLAG_STOP_RENT_DATE
            datePickerFragment.show(supportFragmentManager, getString(R.string.rent_stop_date))
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

    // Initialize setup for bottom sheet navigation
    private fun bottomSheetInitialize() {
        bottomSheet = orderBinding.bottomSheetOrder
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)
    }

    // bottom sheet order confirmation
    private fun orderConfirmationBottomSheet() {
        val bottomSheetOptionOrderConfirmationBinding =
            BottomSheetOrderConfirmationBinding.inflate(layoutInflater)
        val viewBottomSheet = bottomSheetOptionOrderConfirmationBinding.root

        sheetDialog = BottomSheetDialog(this).apply {
            setContentView(viewBottomSheet)
            show()
            setOnDismissListener { sheetDialog = null }
        }

        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetOptionOrderConfirmationBinding.btnAcceptOrder.setOnClickListener {
            sheetDialog?.dismiss()
        }

        bottomSheetOptionOrderConfirmationBinding.btnCancelOrder.setOnClickListener {
            sheetDialog?.dismiss()
        }
    }
}