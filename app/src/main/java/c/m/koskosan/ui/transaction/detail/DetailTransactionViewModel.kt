package c.m.koskosan.ui.transaction.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.model.OrderResponse
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.vo.ResponseState

class DetailTransactionViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    // get detail user order detail by order uid
    private lateinit var _orderUidInput: String

    fun setOrderUid(orderUid: String) {
        this._orderUidInput = orderUid
    }

    fun getOrderDetailByOrderUid(): LiveData<ResponseState<OrderResponse>> =
        firebaseRepository.readOrderDetailByOrderUid(_orderUidInput)
}