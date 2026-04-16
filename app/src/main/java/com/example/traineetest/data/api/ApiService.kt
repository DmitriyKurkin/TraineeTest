package com.example.traineetest.data.api
import com.example.traineetest.data.model.User
import retrofit2.http.GET
interface ApiService{
    @GET("users")
    suspend fun getUsers(): List<User>
}
