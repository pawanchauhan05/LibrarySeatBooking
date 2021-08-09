package com.app.libraryseatbooking

import com.app.libraryseatbooking.pojo.ServerResponse
import java.net.SocketTimeoutException

object FakeResponseUtility {
    private val ex : Exception = SocketTimeoutException("TIMEOUT ERROR!")

    fun getResponseWithError() : Exception {
        return ex
    }

    fun getSuccessResponse() : ServerResponse {
        return ServerResponse(true)
    }
}