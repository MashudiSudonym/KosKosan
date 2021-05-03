package c.m.koskosan.ui.transaction.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import c.m.koskosan.R
import c.m.koskosan.databinding.ActivityDetailTransactionBinding
import c.m.koskosan.util.*
import c.m.koskosan.util.Constants.Companion.ACCEPT_STATUS
import c.m.koskosan.util.Constants.Companion.CANCEL_STATUS
import c.m.koskosan.util.Constants.Companion.SURVEY_STATUS
import c.m.koskosan.util.Constants.Companion.WAITING_STATUS
import c.m.koskosan.vo.ResponseState
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailTransactionActivity : AppCompatActivity() {
    private val detailTransactionViewModel: DetailTransactionViewModel by viewModel()
    private lateinit var detailTransactionBinding: ActivityDetailTransactionBinding
    private lateinit var layout: View
    private var uid: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // viewBinding initialize
        detailTransactionBinding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        val view = detailTransactionBinding.root
        setContentView(view)

        // initialize layout for handle view utilities
        layout = view

        // get parsing transaction uid
        val intent = intent
        uid = intent.getStringExtra(Constants.UID)

        // AppBar / ActionBar title setup
        setSupportActionBar(detailTransactionBinding.toolbarDetailTransaction)
        supportActionBar?.apply {
            title = getString(R.string.detail_transaction_title)
            setDisplayHomeAsUpEnabled(true)
        }

        // initialize get detail transaction
        initializeGetDetailTransactionByOrderUid()

        // initialize swipe to refresh data
        detailTransactionBinding.detailTransactionSwipeRefreshView.setOnRefreshListener {
            detailTransactionBinding.detailTransactionSwipeRefreshView.isRefreshing = false

            // get data
            initializeGetDetailTransactionByOrderUid()
        }
    }

    // initialize get detail transaction
    private fun initializeGetDetailTransactionByOrderUid() {
        detailTransactionViewModel.setOrderUid(uid.toString())
        detailTransactionViewModel.getOrderDetailByOrderUid()
            .observe(this, { response ->
                if (response != null) when (response) {
                    is ResponseState.Error -> showErrorStateView() // error state
                    is ResponseState.Loading -> showLoadingStateView() // loading state
                    is ResponseState.Success -> {
                        // success state
                        showSuccessStateView()

                        // show data to view
                        val data = response.data

                        with(detailTransactionBinding) {
                            tvOrderCreatedAt.text = data?.orderCreated
                            tvOrderStatus.text = when (data?.orderStatus) {
                                WAITING_STATUS -> getString(R.string.waiting_status)
                                SURVEY_STATUS -> getString(R.string.survey_status)
                                ACCEPT_STATUS -> getString(R.string.accept_status)
                                CANCEL_STATUS -> getString(R.string.cancel_status)
                                else -> getString(R.string.data_error_null)
                            }
                            tvSurveyDate.text = data?.surveySchedule
                            tvRentStartDate.text = data?.rentStart
                            tvRentStopDate.text = data?.rentStop
                            tvNameUserTransaction.text = data?.userName
                            tvAddressUserTransaction.text = data?.userAddress
                            tvPhoneUserTransaction.text = data?.userPhone
                            tvNameLocationTransaction.text = data?.nameLocation
                            tvAddressLocationTransaction.text = data?.addressLocation
                            tvPhoneLocationTransaction.text = data?.phoneLocation

                            // contact admin button
                            btnCall.setOnClickListener {
                                sendMessageWhatsApp(
                                    this@DetailTransactionActivity,
                                    "whatsapp://api.whatsapp.com/send?phone=${data?.phoneLocation?.replace("+", "")}&text=Halo ${data?.nameLocation},saya ingin tanya tentang kos." +
                                            "\nAtas nama : ${data?.userName}"
                                )
                            }
                        }
                    }
                }
            })
    }

    // handle success state of view
    private fun showSuccessStateView() {
        detailTransactionBinding.animLoading.gone()
        detailTransactionBinding.animError.gone()
        detailTransactionBinding.detailTransactionLayout.visible()
    }

    // handle error state of view
    private fun showErrorStateView() {
        detailTransactionBinding.animError.visible()
        detailTransactionBinding.animLoading.gone()
        detailTransactionBinding.detailTransactionLayout.invisible()
    }

    // handle loading state of view
    private fun showLoadingStateView() {
        detailTransactionBinding.detailTransactionLayout.invisible()
        detailTransactionBinding.animLoading.visible()
        detailTransactionBinding.animError.gone()
    }

    // activate back button arrow
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}