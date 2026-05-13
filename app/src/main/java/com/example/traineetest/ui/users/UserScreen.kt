package com.example.traineetest.ui.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.traineetest.data.model.Department
import com.example.traineetest.data.model.User
import com.example.traineetest.viewmodel.ErrorReason
import com.example.traineetest.viewmodel.UsersListRow
import com.example.traineetest.viewmodel.UsersSortType
import com.example.traineetest.viewmodel.UsersUiEvent
import com.example.traineetest.viewmodel.UsersUiState
import com.example.traineetest.viewmodel.UserViewModel
import com.example.traineetest.viewmodel.criticalSubtitle
import com.example.traineetest.viewmodel.criticalTitle

@Composable
fun UsersScreen(
    viewModel: UserViewModel,
    onUserClick: (String) -> Unit,
    onMapClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UsersUiEvent.ShowErrorSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    UsersScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onRetryClick = viewModel::loadInitial,
        onRefresh = viewModel::refreshUsers,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onDepartmentSelected = viewModel::onDepartmentSelected,
        onSortTypeSelected = viewModel::onSortTypeSelected,
        onUserClick = onUserClick,
        onMapClick = onMapClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsersScreenContent(
    state: UsersUiState,
    snackbarHostState: SnackbarHostState,
    onRetryClick: () -> Unit,
    onRefresh: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onDepartmentSelected: (Department) -> Unit,
    onSortTypeSelected: (UsersSortType) -> Unit,
    onUserClick: (String) -> Unit,
    onMapClick: () -> Unit
) {
    var showSortSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Сотрудники") },
                actions = {
                    IconButton(onClick = onMapClick) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Открыть карту"
                        )
                    }

                    val canSort = state is UsersUiState.Content || state is UsersUiState.EmptySearch
                    IconButton(
                        enabled = canSort,
                        onClick = { showSortSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Сортировка"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        when (state) {
            UsersUiState.Loading -> {
                LoadingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is UsersUiState.CriticalError -> {
                CriticalErrorScreen(
                    reason = state.reason,
                    onRetryClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is UsersUiState.Content -> {
                UsersContentScreen(
                    state = state,
                    onRefresh = onRefresh,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onDepartmentSelected = onDepartmentSelected,
                    onUserClick = onUserClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )

                if (showSortSheet) {
                    SortBottomSheet(
                        selectedSortType = state.sortType,
                        onSortTypeSelected = onSortTypeSelected,
                        onDismiss = { showSortSheet = false }
                    )
                }
            }

            is UsersUiState.EmptySearch -> {
                EmptySearchScreen(
                    state = state,
                    onRefresh = onRefresh,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onDepartmentSelected = onDepartmentSelected,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )

                if (showSortSheet) {
                    SortBottomSheet(
                        selectedSortType = state.sortType,
                        onSortTypeSelected = onSortTypeSelected,
                        onDismiss = { showSortSheet = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CriticalErrorScreen(
    reason: ErrorReason,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = reason.criticalTitle(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = reason.criticalSubtitle(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        AssistChip(
            onClick = onRetryClick,
            label = { Text("Повторить") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsersContentScreen(
    state: UsersUiState.Content,
    onRefresh: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onDepartmentSelected: (Department) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SearchField(
            query = state.searchQuery,
            onQueryChanged = onSearchQueryChanged
        )

        DepartmentsTabs(
            departments = state.departments,
            selectedDepartment = state.selectedDepartment,
            onDepartmentSelected = onDepartmentSelected
        )

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(
                    items = state.rows,
                    key = { index, row ->
                        when (row) {
                            is UsersListRow.UserItem -> row.user.id
                            is UsersListRow.YearSeparator -> "year-${row.year}-$index"
                        }
                    }
                ) { _, row ->
                    when (row) {
                        is UsersListRow.UserItem -> {
                            UserListItem(
                                user = row.user,
                                birthdayText = row.birthdayText,
                                onClick = { onUserClick(row.user.id) }
                            )
                        }

                        is UsersListRow.YearSeparator -> {
                            YearSeparator(year = row.year)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptySearchScreen(
    state: UsersUiState.EmptySearch,
    onRefresh: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onDepartmentSelected: (Department) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SearchField(
            query = state.searchQuery,
            onQueryChanged = onSearchQueryChanged
        )

        DepartmentsTabs(
            departments = state.departments,
            selectedDepartment = state.selectedDepartment,
            onDepartmentSelected = onDepartmentSelected
        )

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Ничего не нашли",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Попробуй изменить запрос или выбрать другой отдел.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true,
        placeholder = {
            Text("Введи имя, фамилию или ник")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Очистить поиск"
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors()
    )
}

@Composable
private fun DepartmentsTabs(
    departments: List<Department>,
    selectedDepartment: Department,
    onDepartmentSelected: (Department) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = departments.indexOf(selectedDepartment).coerceAtLeast(0),
        edgePadding = 16.dp
    ) {
        departments.forEach { department ->
            Tab(
                selected = selectedDepartment == department,
                onClick = { onDepartmentSelected(department) },
                text = {
                    Text(department.displayName)
                }
            )
        }
    }
}

@Composable
private fun UserListItem(
    user: User,
    birthdayText: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = user.userTag.lowercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = Department.fromApiValue(user.department).displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (birthdayText != null) {
            Text(
                text = birthdayText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun YearSeparator(
    year: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(modifier = Modifier.weight(1f))

        Text(
            text = year.toString(),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Divider(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortBottomSheet(
    selectedSortType: UsersSortType,
    onSortTypeSelected: (UsersSortType) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Text(
            text = "Сортировка",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        SortOptionRow(
            title = "По алфавиту",
            selected = selectedSortType == UsersSortType.ALPHABET,
            onClick = {
                onSortTypeSelected(UsersSortType.ALPHABET)
                onDismiss()
            }
        )

        SortOptionRow(
            title = "По дню рождения",
            selected = selectedSortType == UsersSortType.BIRTHDAY,
            onClick = {
                onSortTypeSelected(UsersSortType.BIRTHDAY)
                onDismiss()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SortOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}