package c.m.koskosan.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import c.m.koskosan.data.model.OrderResponse
import c.m.koskosan.data.repository.AuthRepository
import c.m.koskosan.data.repository.FirebaseRepository
import c.m.koskosan.vo.ResponseState

class TransactionViewModel(
    private val authRepository: AuthRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    // Get User active uid
    private val userUID: LiveData<String> = authRepository.getUserUid()

    // get user transaction order list
    fun getUserOrderByUid(): LiveData<ResponseState<List<OrderResponse>>> =
        firebaseRepository.readOrderByUid(userUID.value.toString())
}