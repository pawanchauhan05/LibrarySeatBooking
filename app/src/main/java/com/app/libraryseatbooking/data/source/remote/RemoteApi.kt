package com.app.libraryseatbooking.data.source.remote

import com.app.libraryseatbooking.pojo.ServerResponse
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface RemoteApi {

    @POST("submit-session")
    suspend fun submitSession(@QueryMap queryParams : Map<String, String>) : ServerResponse
}