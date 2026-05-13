package com.example.traineetest.viewmodel


import com.example.traineetest.data.model.Department
import com.example.traineetest.data.model.User

sealed interface UsersUiState {
    data object Loading : UsersUiState

    data class Content(
        val rows: List<UsersListRow>,
        val departments: List<Department>,
        val selectedDepartment: Department,
        val searchQuery: String,
        val sortType: UsersSortType,
        val isRefreshing: Boolean
    ) : UsersUiState

    data class EmptySearch(
        val departments: List<Department>,
        val selectedDepartment: Department,
        val searchQuery: String,
        val sortType: UsersSortType,
        val isRefreshing: Boolean
    ) : UsersUiState

    data class CriticalError(
        val reason: ErrorReason
    ) : UsersUiState
}

sealed interface UsersListRow {
    data class UserItem(
        val user: User,
        val birthdayText: String?
    ) : UsersListRow

    data class YearSeparator(
        val year: Int
    ) : UsersListRow
}

sealed interface UsersUiEvent {
    data class ShowErrorSnackbar(val message: String) : UsersUiEvent
}