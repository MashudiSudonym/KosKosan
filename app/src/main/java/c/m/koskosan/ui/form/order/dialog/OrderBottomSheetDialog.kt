package c.m.koskosan.ui.form.order.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import c.m.koskosan.databinding.FragmentBottomSheetOrderConfirmationBinding
import c.m.koskosan.ui.form.order.OrderViewModel
import c.m.koskosan.util.gone
import c.m.koskosan.util.visible
import c.m.koskosan.vo.ResponseState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*


class OrderBottomSheetDialog : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetOrderConfirmationBinding? = null
    private val binding get() = _binding!!
    private val orderViewModel by sharedViewModel<OrderViewModel>()
    private var userName: String? = null
    private var userPhone: String? = null
    private var userAddress: String? = null
    private var locationUID: String? = null
    private var locationName: String? = null
    private var locationAddress: String? = null
    private var locationPhone: String? = null
    private var surveyScheduleDate: String? = null
    private var startRentDate: String? = null
    private var stopRentDate: String? = null
    private var orderCreated: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetOrderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setCanceledOnTouchOutside(false)
            setOnShowListener {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get date time now
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("d-MMMM-yyyy")
        val date = format.format(calendar.time)
        orderCreated = date

        // get user data
        orderViewModel.getUserProfile().observe(viewLifecycleOwner, { result ->
            userName = result.data?.name
            userPhone = result.data?.phoneNumber
            userAddress = result.data?.address

            binding.tvNameOrder.text = userName
            binding.tvPhoneOrder.text = userPhone
        })

        // get location UID
        orderViewModel.locationUID.observe(viewLifecycleOwner, { result ->
            locationUID = result
        })

        // get location name
        orderViewModel.locationName.observe(viewLifecycleOwner, { result ->
            locationName = result
            binding.tvLocationNameOrder.text = locationName
        })

        // get location address
        orderViewModel.locationAddress.observe(viewLifecycleOwner, { result ->
            locationAddress = result
            binding.tvLocationAddressOrder.text = locationAddress
        })

        // get location phone
        orderViewModel.locationPhone.observe(viewLifecycleOwner, { result ->
            locationPhone = result
            binding.tvLocationPhoneOrder.text = locationPhone
        })

        // get survey schedule date
        orderViewModel.surveyScheduleDate.observe(viewLifecycleOwner, { result ->
            surveyScheduleDate = result
            binding.tvSurveyDateOrder.text = surveyScheduleDate
        })

        // get start rent date
        orderViewModel.startRentDate.observe(viewLifecycleOwner, { result ->
            startRentDate = result
            binding.tvStartRentDate.text = startRentDate
        })

        // get stop rent date
        orderViewModel.stopRentDate.observe(viewLifecycleOwner, { result ->
            stopRentDate = result
            binding.tvStopRentDate.text = stopRentDate
        })

        // send order button to database
        binding.btnAcceptOrder.setOnClickListener {
            orderViewModel.setUserOrderData(
                userName.toString(),
                userAddress.toString(),
                userPhone.toString(),
                orderCreated.toString(),
                0,
            )
            orderViewModel.postOrder().observe(viewLifecycleOwner, { response ->
                if (response != null) when (response) {
                    is ResponseState.Error -> showErrorStateView()
                    is ResponseState.Loading -> showLoadingStateView()
                    is ResponseState.Success -> {
                        dismiss()
                        requireActivity().onBackPressed()
                    }
                }
            })
        }

        // cancel button
        binding.btnCancelOrder.setOnClickListener { dismiss() }
    }

    // error state
    private fun showErrorStateView() {
        binding.scrollView.gone()
        binding.animLoading.gone()
        binding.animError.visible()
    }

    // loading state
    private fun showLoadingStateView() {
        binding.scrollView.gone()
        binding.animError.gone()
        binding.animLoading.visible()
    }
}