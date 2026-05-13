package com.example.traineetest.data.repository

import com.example.traineetest.data.api.RetrofitClient
import com.example.traineetest.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {

    suspend fun getUsers(): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                RetrofitClient.api.getUsers().items
            }
        }
    }
}