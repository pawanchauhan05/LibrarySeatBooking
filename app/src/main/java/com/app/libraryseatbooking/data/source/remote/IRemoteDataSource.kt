package com.app.libraryseatbooking.data.source.remote

import com.app.libraryseatbooking.pojo.ServerResponse

interface IRemoteDataSource {

    suspend fun submitSession(queryParams : Map<String, String>): ServerResponse
}