package com.example.traineetest.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.traineetest.ui.map.MapScreen
import com.example.traineetest.ui.users.UserDetailsScreen
import com.example.traineetest.ui.users.UsersScreen
import com.example.traineetest.viewmodel.UserViewModel

private object Routes {
    const val USERS = "users"
    const val USER_DETAILS = "userDetails/{userId}"
    const val MAP = "map"

    fun userDetails(userId: String): String = "userDetails/$userId"
}

@Composable
fun AppNavGraph(
    userViewModel: UserViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.USERS
    ) {
        composable(Routes.USERS) {
            UsersScreen(
                viewModel = userViewModel,
                onUserClick = { userId ->
                    navController.navigate(Routes.userDetails(userId))
                },
                onMapClick = {
                    navController.navigate(Routes.MAP)
                }
            )
        }

        composable(
            route = Routes.USER_DETAILS,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId").orEmpty()
            val selectedUser = userViewModel.getSelectedUser(userId)

            UserDetailsScreen(
                user = selectedUser,
                onBackClick = navController::popBackStack
            )
        }

        composable(Routes.MAP) {
            MapScreen(
                onBackClick = navController::popBackStack
            )
        }
    }
}