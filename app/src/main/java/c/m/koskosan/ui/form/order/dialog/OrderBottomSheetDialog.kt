package c.m.koskosan.ui.form.order.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import c.m.koskosan.databinding.FragmentBottomSheetOrderConfirmationBinding
import c.m.koskosan.ui.form.order.OrderViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class OrderBottomSheetDialog : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetOrderConfirmationBinding? = null
    private val binding get() = _binding!!
    private val orderViewModel by sharedViewModel<OrderViewModel>()
    private var userUID: String? = ""
    private var userName: String? = ""
    private var userPhone: String? = ""
    private var locationUID: String? = ""
    private var locationName: String? = ""
    private var locationAddress: String? = ""
    private var locationPhone: String? = ""
    private var surveyScheduleDate: String? = ""
    private var startRentDate: String? = ""
    private var stopRentDate: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetOrderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get location UID
        orderViewModel.locationUID.observe(viewLifecycleOwner, { result ->
            userUID = result
        })

        // get location name
        orderViewModel.locationName.observe(viewLifecycleOwner, {result ->
            locationName = result
            binding.tvLocationNameOrder.text = result
        })

        // get location address
        orderViewModel.locationAddress.observe(viewLifecycleOwner, { result ->
            locationAddress = result
            binding.tvLocationAddressOrder.text = result
        })

        // get location phone
        orderViewModel.locationPhone.observe(viewLifecycleOwner, { result ->
            locationPhone = result
            binding.tvLocationPhoneOrder.text = result
        })

        // get survey schedule date
        orderViewModel.surveyScheduleDate.observe(viewLifecycleOwner, { result ->
            surveyScheduleDate = result
            binding.tvSurveyDateOrder.text = result
        })

        // get start rent date
        orderViewModel.startRentDate.observe(viewLifecycleOwner, { result ->
            startRentDate = result
            binding.tvStartRentDate.text = result
        })

        // get stop rent date
        orderViewModel.startRentDate.observe(viewLifecycleOwner, { result ->
            stopRentDate = result
            binding.tvStopRentDate.text = result
        })

        // send order button to database
        binding.btnAcceptOrder.setOnClickListener {
            dismiss()
        }

        // cancel button
        binding.btnCancelOrder.setOnClickListener { dismiss() }
    }
}