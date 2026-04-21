package com.example.traineetest.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.traineetest.R
import android.widget.Toast
import com.example.traineetest.data.repository.NetworkUtils
import android.widget.Button
class NoInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.no_internet_activity)
        val buttonRetry = findViewById<Button>(R.id.buttonRetry)

        buttonRetry.setOnClickListener {
            if (NetworkUtils.isInternetAvailable(this)) {
                recreate()
            } else {
                Toast.makeText(this, "Интернет всё ещё недоступен", Toast.LENGTH_SHORT).show()
            }
        }
    }
}