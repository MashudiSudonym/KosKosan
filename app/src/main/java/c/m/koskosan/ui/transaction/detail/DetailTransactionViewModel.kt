package c.m.koskosan.ui.transaction.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.model.OrderResponse
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.vo.ResponseState

class DetailTransactionViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    // get detail user order detail by order uid
    fun getOrderDetailByOrderUid(uid: String): LiveData<ResponseState<OrderResponse>> =
        firebaseRepository.readOrderDetailByOrderUid(uid)
}