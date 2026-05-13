package com.example.traineetest.data.api

import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    @Headers(
        "Accept: application/json",
        "Prefer: code=200, example=success"
    )
    @GET("users")
    suspend fun getUsers(): UsersResponse
}