package com.example.traineetest.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.traineetest.data.repository.NetworkUtils
import com.example.traineetest.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!NetworkUtils.isInternetAvailable(this)) {
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
            return
        } else {
            Toast.makeText(this, "Секундочку, гружусь...", Toast.LENGTH_SHORT).show()
        }

        viewModel.loadUsers()

        setContent {
            UserScreen(
                viewModel = viewModel,
                onNoInternet = {
                    startActivity(Intent(this, NoInternetActivity::class.java))
                    finish()
                }
            )
        }
    }
}