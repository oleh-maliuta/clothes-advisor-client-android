package com.olehmaliuta.clothesadvisor.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import com.olehmaliuta.clothesadvisor.utils.navigation.Router
import com.olehmaliuta.clothesadvisor.utils.AppConstants

@Composable
fun BottomBar(
    router: Router
) {
    NavigationBar {
        val navBackStackEntry by router.currentBackStackEntryAsState()

        AppConstants.navItems.forEach { navItem ->
            val testTagName =
                "bottom_bar__navigation_button__${navItem.route.name}"

            NavigationBarItem(
                selected = navBackStackEntry?.destination?.hierarchy?.any {
                    it.route == navItem.route.name
                } == true,
                onClick = {
                    router.navigate(route = navItem.route.name)
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
                        text = stringResource(navItem.labelId),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                },
                modifier = Modifier
                    .testTag(testTagName)
            )
        }
    }
}