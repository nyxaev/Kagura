package com.kagura.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kagura.app.ui.screens.detail.DetailScreen
import com.kagura.app.ui.screens.detail.DetailViewModel
import com.kagura.app.ui.screens.dex.DexScreen
import com.kagura.app.ui.screens.dex.DexViewModel
import com.kagura.app.ui.screens.gacha.GachaScreen
import com.kagura.app.ui.screens.gacha.GachaViewModel
import com.kagura.app.ui.screens.profile.ProfileScreen
import com.kagura.app.ui.screens.profile.ProfileViewModel
import com.kagura.app.ui.screens.search.SearchScreen
import com.kagura.app.ui.screens.search.SearchViewModel
import com.kagura.app.ui.screens.welcome.WelcomeScreen
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kagura.app.ui.theme.KaguraGlassBorder
import com.kagura.app.ui.theme.KaguraGlassSurface
import com.kagura.app.ui.theme.KaguraMuted
import com.kagura.app.ui.theme.KaguraPink
import com.kagura.app.ui.theme.KaguraText
import com.kagura.app.ui.theme.KaguraViolet

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon

private data class BottomTab(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)

private val bottomTabs = listOf(
    BottomTab(NavRoutes.GACHA, Icons.Outlined.Refresh, "Sorteio"),
    BottomTab(NavRoutes.DEX, Icons.Outlined.Info, "Dex"),
    BottomTab(NavRoutes.SEARCH, Icons.Outlined.Search, "Busca"),
    BottomTab(NavRoutes.PROFILE, Icons.Outlined.Person, "Perfil")
)

@Composable
fun KaguraRoot() {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (route != NavRoutes.DETAIL && route != NavRoutes.WELCOME) {
                NavigationBar(
                    containerColor = KaguraGlassSurface,
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .border(1.dp, KaguraGlassBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    bottomTabs.forEach { tab ->
                        val selected = route == tab.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                nav.navigate(tab.route) {
                                    popUpTo(NavRoutes.GACHA) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = KaguraViolet.copy(alpha = 0.35f),
                                selectedIconColor = KaguraPink,
                                selectedTextColor = KaguraText,
                                unselectedIconColor = KaguraMuted,
                                unselectedTextColor = KaguraMuted
                            )
                        )
                    }
                }
            }
        }
    ) { pad ->
        KaguraNavGraph(nav, Modifier.padding(bottom = pad.calculateBottomPadding()))
    }
}

@Composable
fun KaguraNavGraph(nav: NavHostController, modifier: Modifier = Modifier) {
    val openDetail: (Int) -> Unit = { nav.navigate(NavRoutes.detail(it)) }
    NavHost(nav, startDestination = NavRoutes.WELCOME, modifier = modifier) {
        composable(NavRoutes.WELCOME) {
            WelcomeScreen(onStart = {
                nav.navigate(NavRoutes.GACHA) {
                    popUpTo(NavRoutes.WELCOME) { inclusive = true }
                }
            })
        }
        composable(NavRoutes.GACHA) {
            val vm: GachaViewModel = hiltViewModel()
            GachaScreen(vm, openDetail)
        }
        composable(NavRoutes.DEX) {
            val vm: DexViewModel = hiltViewModel()
            DexScreen(vm, openDetail)
        }
        composable(NavRoutes.SEARCH) {
            val vm: SearchViewModel = hiltViewModel()
            SearchScreen(vm, openDetail)
        }
        composable(NavRoutes.PROFILE) {
            val vm: ProfileViewModel = hiltViewModel()
            ProfileScreen(vm)
        }
        composable(NavRoutes.DETAIL, arguments = listOf(navArgument("id") { type = NavType.IntType })) {
            val vm: DetailViewModel = hiltViewModel()
            DetailScreen(vm, onBack = { nav.popBackStack() })
        }
    }
}
