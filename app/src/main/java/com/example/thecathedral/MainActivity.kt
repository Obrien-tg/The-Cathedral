package com.example.thecathedral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thecathedral.ui.screens.FullScheduleScreen
import com.example.thecathedral.ui.screens.HomeScreen
import com.example.thecathedral.ui.theme.TheCathedralTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheCathedralTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onViewFullSchedule = { navController.navigate("schedule") })
        }
        composable("schedule") {
            FullScheduleScreen(onBack = { navController.popBackStack() })
        }
    }
}
