package com.cedric.fgf

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cedric.fgf.database.FavouritesDatabase
import com.cedric.fgf.database.LatestDatabase
import com.cedric.fgf.misc.BottomNavBar
import com.cedric.fgf.pages.Favourites
import com.cedric.fgf.pages.ListView
import com.cedric.fgf.pages.Settings
import com.cedric.fgf.ui.theme.FGFTheme
//import com.cedric.fgf.NotificationHandler

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val workManager = WorkManager.getInstance(this)
//        // Cancelled the extra workers I wasn't accounting for.
////        workManager.cancelAllWork()
////        workManager.pruneWork()
//        workManager.enqueueUniquePeriodicWork(
//            "updateCheckRequest",
//            ExistingPeriodicWorkPolicy.UPDATE,
//            updateCheckRequest)

//        createNotificationChannel()

        setContent {
            val navController = rememberNavController()
            val db = FavouritesDatabase.getInstance(this)
            val db_li = LatestDatabase.getInstance(this)

            FGFTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
                    Scaffold(
//                        topBar = {
                        // THIS USED TO BE TopAppBar from Material (1), looked better, can't redo as well. Not needed anyway.
//                            CenterAlignedTopAppBar(
//                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
//                                title = {
////                                    Icon(
////                                        painter = painterResource(id = R.drawable.fgf_logo_whiteout),
////                                        contentDescription = "logo",
////                                        tint = MaterialTheme.colorScheme.onPrimary,
////                                        modifier = Modifier.size(50.dp)
////                                    )
//                                    Text(
//                                        text = "FreeGameFindings",
//                                        modifier = Modifier.fillMaxWidth(),
//                                        textAlign = TextAlign.Center,
//                                        color = MaterialTheme.colorScheme.onPrimary
//                                    )
//                                }
//                            )
//                        },
                        bottomBar = {
                            BottomNavigation(backgroundColor = MaterialTheme.colorScheme.primary) {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.destination?.route

                                BottomNavBar.values().forEach { screen ->
                                    BottomNavigationItem(
                                        icon = { Icon(screen.icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onPrimary) },
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
                    ) { innerPadding -> Box(modifier = Modifier.padding(
                            PaddingValues(bottom = innerPadding.calculateBottomPadding())
                        )) {
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
}
