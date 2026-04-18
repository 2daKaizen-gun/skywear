package com.kaizen.skywear.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaizen.skywear.ui.screen.DashboardScreen
import com.kaizen.skywear.ui.screen.SearchScreen

// SkyWear 앱 네비게이션 라우트 정의

// 라우트 상수
sealed class Screen(val route: String) {
    data object Dashboard: Screen("dashboard")
    data object Checklist: Screen("checklist")
    data object Search: Screen("search")
}

// NavGraph 진입점
@Composable
fun SkyWearNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToChecklist = {
                    navController.navigate(Screen.Checklist.route)
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }
        composable(Screen.Checklist.route) {
            ChecklistScreen(
                onBack = {navController.popBackStack()}
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onBack = {navController.popBackStack()}
            )
        }
    }
}