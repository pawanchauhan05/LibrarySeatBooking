package com.app.libraryseatbooking.data.source.remote

import com.app.libraryseatbooking.pojo.ServerResponse

class RemoteDataSource(private val remoteApi: RemoteApi) : IRemoteDataSource {

    override suspend fun submitSession(queryParams: Map<String, String>): ServerResponse {
        return remoteApi.submitSession(queryParams)
    }
}