package com.example.traineetest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traineetest.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        adapter = UserAdapter()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.users.collect {
                adapter.submitList(it)
            }
        }

        viewModel.loadUsers()
    }
}