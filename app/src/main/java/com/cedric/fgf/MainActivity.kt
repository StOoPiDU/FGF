package com.cedric.fgf

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cedric.fgf.ui.theme.FGFTheme
//import com.cedric.fgf.NotificationHandler

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        createNotificationChannel()
        setContent {
            val navController = rememberNavController()
            val db = FavouritesDatabase.getInstance(this)
//            val db_li = LatestDatabase.getInstance(this)

            FGFTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Blue) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                backgroundColor = Color.Blue,
                                title = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.fgf_logo_whiteout),
                                        contentDescription = "logo",
                                        tint = Color.White,
                                    )
                                    Text(
                                        text = "FreeGameFindings",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                }
                            )
                        },
                        bottomBar = {
                            BottomNavigation(backgroundColor = Color.Blue) {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.destination?.route

                                BottomNavBar.values().forEach { screen ->
                                    BottomNavigationItem(
                                        icon = { Icon(screen.icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.White) },
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = BottomNavBar.Home.route
                        ) {
                            composable(BottomNavBar.Home.route) {
                                ListView.DisplayListView()
                            }
                            composable(BottomNavBar.Favourites.route) {
                                Favourites.DisplayFavouritesView()
                            }
                            composable(BottomNavBar.Settings.route) {
                                Settings.SettingsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
