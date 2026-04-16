package com.example.traineetest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.launch
import com.example.traineetest.viewmodel.UserAdapter
import com.example.traineetest.data.api.RetrofitClient
import com.example.traineetest.ui.UserAdapter
import com.example.traineetest.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: UserAdapter
    private lateinit var viewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = UserAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        lifecycleScope.launch {
            viewModel.users.collect {
                adapter.submitList(it)
            }
        }
        viewModel.loadUsers()
    }
}