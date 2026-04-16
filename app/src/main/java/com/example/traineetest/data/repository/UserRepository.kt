package com.example.traineetest.data.repository

import com.example.traineetest.data.api.RetrofitClient
import com.example.traineetest.data.model.User

class UserRepository {

    suspend fun getUsers(): List<User> {
        return RetrofitClient.api.getUsers().items
    }
}