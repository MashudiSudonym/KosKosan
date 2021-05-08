package c.m.koskosan.vo

/**
 * Sealed class, for helping application data status (state management)
 */

sealed class ResponseState<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : ResponseState<T>(data)
    class Error<T>(message: String?, data: T? = null) : ResponseState<T>(data, message)
    class Loading<T>(data: T?) : ResponseState<T>(data)
}
