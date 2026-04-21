package com.example.traineetest.ui

import android.content.Intent
import com.example.traineetest.ui.NoInternetActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traineetest.data.model.User
import com.example.traineetest.data.model.UserFilter
import com.example.traineetest.data.repository.NetworkUtils
import com.example.traineetest.viewmodel.UserViewModel
import com.example.traineetest.R

class MainActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapter: UserAdapter
    private var allUser: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (!NetworkUtils.isInternetAvailable(this)) {
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
            return
        } else {
            Toast.makeText(this, "Секундочку, гружусь...", Toast.LENGTH_SHORT).show()
        }

        adapter = UserAdapter()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val searchView = findViewById<SearchView>(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                val filtered = UserFilter.filter(allUser, query.orEmpty())
                adapter.submitList(filtered)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = UserFilter.filter(allUser, newText.orEmpty())
                adapter.submitList(filtered)
                return true
            }
        })

        lifecycleScope.launchWhenStarted {
            viewModel.users.collect {
                allUser = it
                adapter.submitList(it)
            }
        }

        viewModel.loadUsers()
    }
}