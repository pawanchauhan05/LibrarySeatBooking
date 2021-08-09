package com.app.libraryseatbooking.data.source.remote

import com.app.libraryseatbooking.FakeResponseUtility
import com.app.libraryseatbooking.pojo.ServerResponse

class FakeRemoteDataSource : IRemoteDataSource {

    enum class Data {
        SHOULD_RETURN_ERROR,
        SHOULD_RETURN_SUCCESS
    }

    private var status = Data.SHOULD_RETURN_SUCCESS

    fun setStatus(value: Data) {
        status = value
    }

    override suspend fun submitSession(queryParams: Map<String, String>): ServerResponse {
        return when(status) {
            Data.SHOULD_RETURN_SUCCESS -> FakeResponseUtility.getSuccessResponse()
            Data.SHOULD_RETURN_ERROR -> throw FakeResponseUtility.getResponseWithError()
        }
    }
}