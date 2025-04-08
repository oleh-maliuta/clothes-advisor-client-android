package com.olehmaliuta.clothesadvisor.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.olehmaliuta.clothesadvisor.navigation.NavItem
import com.olehmaliuta.clothesadvisor.navigation.Router

@Composable
fun BottomBar(router: Router, navItems: List<NavItem>) {
    NavigationBar {
        val navBackStackEntry by router.controller.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        navItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any {
                    it.route == navItem.route.name
                } == true,
                onClick = {
                    router.navigateTo(navItem.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = navItem.iconId),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                label = {
                    Text(
                        text = navItem.label,
                        fontSize = 11.sp
                    )
                }
            )
        }
    }
}