package com.haiphong.todoapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.haiphong.todoapp.ui.home.HomeDestination
import com.haiphong.todoapp.ui.home.HomeScreen
import com.haiphong.todoapp.ui.home.HomeScreenViewModel

@Composable
fun ToDoNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val homeViewModel: HomeScreenViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(homeScreenViewModel = homeViewModel)
        }
    }
}