package org.kts.tazmin.core.presentation

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import org.kts.tazmin.navigation.Screen

sealed class BottomNavItem(
    val route: Screen,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null
) {
    object Courses : BottomNavItem(
        route = Screen.Courses,
        title = "Курсы",
        icon = Icons.AutoMirrored.Filled.MenuBook,
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook
    )

    //
//    object Favorites : BottomNavItem(
//        route = Screen.Favorites.route,
//        title = "Избранное",
//        icon = Icons.Default.FavoriteBorder,
//        selectedIcon = Icons.Default.Favorite
//    )
    object Catalog : BottomNavItem(
        route = Screen.Catalog,
        title = "Каталог",
        icon = Icons.Default.Search,
        selectedIcon = Icons.Default.Search
    )

    object Profile : BottomNavItem(
        route = Screen.Profile,
        title = "Профиль",
        icon = Icons.Default.PersonOutline,
        selectedIcon = Icons.Default.Person
    )
}

@Composable
fun BottomNavBar(
    navController: NavController,
    items: List<BottomNavItem> = listOf(
        BottomNavItem.Courses,
        BottomNavItem.Catalog,
        //BottomNavItem.Favorites,
        BottomNavItem.Profile
    )
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier.height(64.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val selected = currentDestination?.hasRoute(item.route::class) == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected && item.selectedIcon != null)
                            item.selectedIcon
                        else
                            item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
