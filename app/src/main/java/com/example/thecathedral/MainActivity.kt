package com.example.thecathedral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thecathedral.data.DataStoreManager
import com.example.thecathedral.data.ScheduleRepository
import com.example.thecathedral.navigation.HomeRoute
import com.example.thecathedral.navigation.ScheduleRoute
import com.example.thecathedral.ui.screens.FullScheduleScreen
import com.example.thecathedral.ui.screens.HomeScreen
import com.example.thecathedral.ui.theme.TheCathedralTheme
import com.example.thecathedral.viewmodel.ScheduleViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheCathedralTheme {
                val navController = rememberNavController()

                val repository = remember {
                    ScheduleRepository(DataStoreManager(applicationContext))
                }
                val factory = remember {
                    ScheduleViewModelFactory(repository)
                }

                NavHost(
                    navController = navController,
                    startDestination = HomeRoute
                ) {
                    composable<HomeRoute> {
                        HomeScreen(
                            viewModel = viewModel(factory = factory),
                            onViewFullSchedule = {
                                navController.navigate(ScheduleRoute)
                            }
                        )
                    }
                    composable<ScheduleRoute> {
                        FullScheduleScreen(
                            viewModel = viewModel(factory = factory),
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
