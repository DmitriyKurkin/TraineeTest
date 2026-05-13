package com.example.traineetest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traineetest.data.model.Department
import com.example.traineetest.data.model.User
import com.example.traineetest.data.model.UserFilter
import com.example.traineetest.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.MonthDay
import java.time.format.DateTimeFormatter
import java.util.Locale

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _uiState = MutableStateFlow<UsersUiState>(UsersUiState.Loading)
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UsersUiEvent>()
    val events: SharedFlow<UsersUiEvent> = _events.asSharedFlow()

    private var sourceUsers: List<User> = emptyList()
    private var searchQuery: String = ""
    private var selectedDepartment: Department = Department.ALL
    private var sortType: UsersSortType = UsersSortType.ALPHABET

    init {
        loadInitial()
    }

    fun getSelectedUser(userId: String): User? {
        return sourceUsers.firstOrNull { user ->
            user.id == userId
        }
    }

    fun loadInitial() {
        viewModelScope.launch {
            _uiState.value = UsersUiState.Loading

            repository.getUsers()
                .onSuccess { users ->
                    sourceUsers = users
                    reduceContent(isRefreshing = false)
                }
                .onFailure { throwable ->
                    _uiState.value = UsersUiState.CriticalError(throwable.toErrorReason())
                }
        }
    }

    fun refreshUsers() {
        val currentState = _uiState.value
        if (currentState !is UsersUiState.Content && currentState !is UsersUiState.EmptySearch) return

        viewModelScope.launch {
            reduceContent(isRefreshing = true)

            val result: Result<List<User>> = repository.getUsers()

            if (result.isSuccess) {
                sourceUsers = result.getOrNull().orEmpty()
                reduceContent(isRefreshing = false)
            } else {
                val throwable = result.exceptionOrNull() ?: IllegalStateException("Unknown error")

                reduceContent(isRefreshing = false)

                _events.emit(
                    UsersUiEvent.ShowErrorSnackbar(
                        throwable.toErrorReason().refreshMessage()
                    )
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        reduceContent()
    }

    fun onDepartmentSelected(department: Department) {
        selectedDepartment = department
        reduceContent()
    }

    fun onSortTypeSelected(newSortType: UsersSortType) {
        sortType = newSortType
        reduceContent()
    }

    private fun reduceContent(isRefreshing: Boolean = false) {
        val filteredUsers = UserFilter.filter(sourceUsers, searchQuery)
            .filterByDepartment(selectedDepartment)
            .sortBy(sortType)

        val rows = filteredUsers.toRows(sortType)

        _uiState.value = if (rows.isEmpty() && searchQuery.isNotBlank()) {
            UsersUiState.EmptySearch(
                departments = Department.visibleTabs,
                selectedDepartment = selectedDepartment,
                searchQuery = searchQuery,
                sortType = sortType,
                isRefreshing = isRefreshing
            )
        } else {
            UsersUiState.Content(
                rows = rows,
                departments = Department.visibleTabs,
                selectedDepartment = selectedDepartment,
                searchQuery = searchQuery,
                sortType = sortType,
                isRefreshing = isRefreshing
            )
        }
    }

    private fun List<User>.filterByDepartment(department: Department): List<User> {
        if (department == Department.ALL) return this
        return filter { user -> Department.fromApiValue(user.department) == department }
    }

    private fun List<User>.sortBy(type: UsersSortType): List<User> {
        return when (type) {
            UsersSortType.ALPHABET -> sortedWith(
                compareBy<User> { it.firstName.lowercase() }
                    .thenBy { it.lastName.lowercase() }
                    .thenBy { it.userTag.lowercase() }
            )

            UsersSortType.BIRTHDAY -> {
                val today = LocalDate.now()
                sortedWith(
                    compareBy<User> { it.nextBirthdayDate(today) }
                        .thenBy { it.firstName.lowercase() }
                        .thenBy { it.lastName.lowercase() }
                )
            }
        }
    }

    private fun List<User>.toRows(type: UsersSortType): List<UsersListRow> {
        if (type == UsersSortType.ALPHABET) {
            return map { user ->
                UsersListRow.UserItem(user = user, birthdayText = null)
            }
        }

        val today = LocalDate.now()
        val birthdayFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ru"))
        val rows = mutableListOf<UsersListRow>()
        var previousYear: Int? = null

        forEach { user ->
            val nextBirthday = user.nextBirthdayDate(today)

            if (previousYear != null && previousYear != nextBirthday.year) {
                rows += UsersListRow.YearSeparator(nextBirthday.year)
            }

            rows += UsersListRow.UserItem(
                user = user,
                birthdayText = nextBirthday.format(birthdayFormatter)
            )

            previousYear = nextBirthday.year
        }

        return rows
    }
}

private fun User.nextBirthdayDate(today: LocalDate): LocalDate {
    val birthdayDate = LocalDate.parse(birthday)
    val monthDay = MonthDay.from(birthdayDate)
    val thisYearBirthday = monthDay.atYear(today.year)

    return if (thisYearBirthday.isBefore(today)) {
        monthDay.atYear(today.year + 1)
    } else {
        thisYearBirthday
    }
}