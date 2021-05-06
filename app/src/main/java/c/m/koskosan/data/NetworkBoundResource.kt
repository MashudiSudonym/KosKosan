package c.m.koskosan.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import c.m.koskosan.util.ContextProviders
import c.m.koskosan.vo.ResponseState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class NetworkBoundResource<ResultType, RequestType> constructor(private val contextProviders: ContextProviders) {
    private val result = MediatorLiveData<ResponseState<ResultType>>()

    init {
        result.value = ResponseState.Loading(null)
        @Suppress("LeakingThis") val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(ResponseState.Success(newData))
                }
            }
        }
    }

    private fun setValue(newValue: ResponseState<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val firebaseResponse = runBlocking { createCall() }
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData ->
            setValue(ResponseState.Loading(newData))
        }
        firebaseResponse?.let {data ->
            result.addSource(data) { response ->
                result.removeSource(data)
                result.removeSource(dbSource)
                when (response) {
                    is ResponseState.Success -> {
                        CoroutineScope(contextProviders.io).launch {
                            processResponse(response)?.let { saveCallResult(it) }
                            CoroutineScope(contextProviders.main).launch {
                                result.addSource(loadFromDb()) { newData ->
                                    setValue(ResponseState.Success(newData))
                                }
                            }
                        }
                    }
                    is ResponseState.Error -> {
                        onFetchFailed()
                        result.addSource(dbSource) { newData ->
                            setValue(ResponseState.Error(response.message, newData))
                        }
                    }
                    is ResponseState.Loading -> {
                        CoroutineScope(contextProviders.main).launch {
                            result.addSource(loadFromDb()) { newData ->
                                setValue(ResponseState.Success(newData))
                            }
                        }
                    }
                }
            }
        }

    }

    protected open fun processResponse(response: ResponseState<RequestType>) = response.data

    protected abstract suspend fun saveCallResult(item: RequestType)

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract fun loadFromDb(): LiveData<ResultType>

    protected abstract fun createCall(): LiveData<ResponseState<RequestType>>?

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<ResponseState<ResultType>>
}