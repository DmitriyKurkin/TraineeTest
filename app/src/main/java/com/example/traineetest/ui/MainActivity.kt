package com.example.traineetest.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traineetest.data.model.User
import com.example.traineetest.data.model.UserFilter
import com.example.traineetest.data.repository.NetworkUtils
import com.example.traineetest.viewmodel.UserViewModel
import com.example.traineetest.R
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.launch

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
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val searchView = findViewById<SearchView>(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        swipeRefresh.setOnRefreshListener {
            Toast.makeText(this, "Список пытается обновиться...", Toast.LENGTH_SHORT).show()
            if (NetworkUtils.isInternetAvailable(this)) {
                viewModel.loadUsers()
            } else {
                swipeRefresh.isRefreshing = false
                startActivity(Intent(this, NoInternetActivity::class.java))
                finish()
            }
        }
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

        lifecycleScope.launch {
            viewModel.users.collect { users ->
                allUser = users
                adapter.submitList(users)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                swipeRefresh.isRefreshing = isLoading
            }
        }

        viewModel.loadUsers()
    }
}