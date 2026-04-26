package com.example.traineetest.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.traineetest.data.model.User
import com.example.traineetest.data.model.UserFilter
import com.example.traineetest.data.repository.NetworkUtils
import com.example.traineetest.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.unit.sp

enum class SortType {
    ALPHABET,
    BIRTHDAY
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserScreen(
    viewModel: UserViewModel,
    onNoInternet: () -> Unit
) {
    val context = LocalContext.current

    val users by viewModel.users.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var sortType by remember { mutableStateOf(SortType.ALPHABET) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var selectedDepartment by remember { mutableStateOf("All") }

    val departments = listOf("All") + users
        .map { it.department }
        .distinct()
        .sorted()

    val searchedUsers = UserFilter.filter(users, searchText)

    val filteredUsers = if (selectedDepartment == "All") {
        searchedUsers
    } else {
        searchedUsers.filter { it.department == selectedDepartment }
    }

    val sortedUsers = when (sortType) {
        SortType.ALPHABET -> filteredUsers.sortedBy {
            "${it.firstName} ${it.lastName}"
        }

        SortType.BIRTHDAY -> filteredUsers.sortedBy {
            it.birthday.takeIf { birthday -> birthday.length >= 10 }?.substring(5) ?: ""
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = {
            Toast.makeText(context, "Список обновляется...", Toast.LENGTH_SHORT).show()

            if (NetworkUtils.isInternetAvailable(context)) {
                viewModel.loadUsers()
            } else {
                onNoInternet()
            }
        }
    )

    MaterialTheme (
        colorScheme = lightColorScheme(
        onBackground = Color.White
    )
    ) {
        if (selectedUser == null) {
            MainUserScreen(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                sortType = sortType,
                onSortTypeChange = { sortType = it },
                departments = departments,
                selectedDepartment = selectedDepartment,
                onDepartmentClick = { selectedDepartment = it },
                users = sortedUsers,
                isLoading = isLoading,
                pullRefreshState = pullRefreshState,
                onUserClick = { selectedUser = it }
            )
        } else {
            UserDetailsScreen(
                user = selectedUser!!,
                onBackClick = {
                    selectedUser = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainUserScreen(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    sortType: SortType,
    onSortTypeChange: (SortType) -> Unit,
    departments: List<String>,
    selectedDepartment: String,
    onDepartmentClick: (String) -> Unit,
    users: List<User>,
    isLoading: Boolean,
    pullRefreshState: androidx.compose.material.pullrefresh.PullRefreshState,
    onUserClick: (User) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = "https://abrakadabra.fun/uploads/posts/2022-03/1647778233_5-abrakadabra-fun-p-cherno-belii-fon-dlya-telefona-7.jpg",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.17f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Поиск сотрудника") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { onExpandedChange(true) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Сортировка"
                            )
                        }
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpandedChange(false) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    DropdownMenuItem(
                        text = { Text("По алфавиту") },
                        onClick = {
                            onSortTypeChange(SortType.ALPHABET)
                            onExpandedChange(false)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("По дню рождения") },
                        onClick = {
                            onSortTypeChange(SortType.BIRTHDAY)
                            onExpandedChange(false)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(departments) { department ->
                    Text(
                        text = department,
                        color = Color.Black,
                        fontWeight = if (department == selectedDepartment) {
                            FontWeight.Bold
                        } else {
                            FontWeight.SemiBold
                        },
                        modifier = Modifier
                            .clickable {
                                onDepartmentClick(department)
                            }
                            .padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                if (users.isEmpty() && searchText.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(96.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Мы никого не нашли",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Попробуйте скорректировать запрос",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(users) { user ->
                            UserListItem(
                                user = user,
                                sortType = sortType,
                                onClick = {
                                    onUserClick(user)
                                }
                            )
                        }
                    }
                }
                PullRefreshIndicator(
                    refreshing = isLoading,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
@Composable
fun UserListItem(
    user: User,
    sortType: SortType,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "${user.firstName} ${user.lastName}",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = user.department,
                style = MaterialTheme.typography.bodyMedium
            )
            if (sortType == SortType.BIRTHDAY) {
                Text(
                    text = formatBirthday(user.birthday),
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun UserDetailsScreen(
    user: User,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Назад"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = user.position,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = user.department,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = user.birthday)
            Text(text = "${calculateAge(user.birthday)} лет")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Телефон"
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(text = user.phone)
        }
    }
}

fun calculateAge(birthday: String): Int {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDate = format.parse(birthday) ?: return 0

        val birthCalendar = Calendar.getInstance().apply {
            time = birthDate
        }

        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        age
    } catch (e: Exception) {
        0
    }
}
fun formatBirthday(date: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMM", Locale("ru"))
        val parsedDate = inputFormat.parse(date)
        parsedDate?.let { outputFormat.format(it) } ?: date
    } catch (e: Exception) {
        date
    }
}