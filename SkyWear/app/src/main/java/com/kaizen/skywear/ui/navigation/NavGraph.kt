package com.kaizen.skywear.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kaizen.skywear.R
import com.kaizen.skywear.ui.screen.ChecklistScreen
import com.kaizen.skywear.ui.screen.DashboardScreen
import com.kaizen.skywear.ui.screen.SearchScreen
import com.kaizen.skywear.ui.viewmodel.CitySearchViewModel
import com.kaizen.skywear.ui.viewmodel.WeatherViewModel

// SkyWear 앱 네비게이션 라우트 정의

// 라우트 상수
sealed class Screen(val route: String) {
    data object Dashboard: Screen("dashboard")
    data object Checklist: Screen("checklist")
    data object Search: Screen("search")
    data object Subscribe: Screen("subscribe")
    data object Journal: Screen("journal")
    data object Season: Screen("season")
}

// Bottom Navigation 아이템
data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    @StringRes val labelRes: Int
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, Icons.Default.WbSunny,       R.string.nav_weather),
    BottomNavItem(Screen.Subscribe, Icons.Default.Notifications,  R.string.nav_subscribe),
    BottomNavItem(Screen.Journal,   Icons.Default.Book,           R.string.nav_journal),
    BottomNavItem(Screen.Season,    Icons.Default.AutoAwesome,    R.string.nav_season),
)

// NavGraph 진입점
@Composable
fun SkyWearNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val citySearchViewModel: CitySearchViewModel = hiltViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Bottom Nav 표시 여부 — Checklist/Search 상세 화면에서는 숨김
    val showBottomBar = currentDestination?.route in listOf(
        Screen.Dashboard.route,
        Screen.Subscribe.route,
        Screen.Journal.route,
        Screen.Season.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any {
                                it.route == item.screen.route
                            } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(item.icon, contentDescription = stringResource(item.labelRes))
                            },
                            label = { Text(stringResource(item.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { _ ->

        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToChecklist = { navController.navigate(Screen.Checklist.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    viewModel = weatherViewModel
                )
            }
            composable(Screen.Checklist.route) {
                ChecklistScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    searchViewModel = citySearchViewModel,
                    weatherViewModel = weatherViewModel
                )
            }
            // 새 탭 화면들
            composable(Screen.Subscribe.route) {
                SubscribeScreen()
            }
            composable(Screen.Journal.route) {
                JournalScreen()
            }
            composable(Screen.Season.route) {
                SeasonScreen()
            }
        }
    }
}