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
import com.example.thecathedral.navigation.FocusModeRoute
import com.example.thecathedral.navigation.HomeRoute
import com.example.thecathedral.navigation.JournalRoute
import com.example.thecathedral.navigation.PhilosophyRoute
import com.example.thecathedral.navigation.ScheduleRoute
import com.example.thecathedral.navigation.SkillTreeRoute
import com.example.thecathedral.ui.screens.FocusModeScreen
import com.example.thecathedral.ui.screens.FullScheduleScreen
import com.example.thecathedral.ui.screens.HomeScreen
import com.example.thecathedral.ui.screens.JournalScreen
import com.example.thecathedral.ui.screens.PhilosophyScreen
import com.example.thecathedral.ui.skilltree.SkillEdge
import com.example.thecathedral.ui.skilltree.SkillNode
import com.example.thecathedral.ui.skilltree.SkillTreeGraph
import com.example.thecathedral.ui.theme.TheCathedralTheme
import com.example.thecathedral.viewmodel.ScheduleViewModelFactory
import androidx.compose.ui.geometry.Offset

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
                            },
                            onFocusMode = {
                                navController.navigate(FocusModeRoute)
                            },
                            onJournal = {
                                navController.navigate(JournalRoute)
                            },
                            onPhilosophy = {
                                navController.navigate(PhilosophyRoute)
                            },
                            onSkillTree = {
                                navController.navigate(SkillTreeRoute)
                            }
                        )
                    }
                    composable<ScheduleRoute> {
                        FullScheduleScreen(
                            viewModel = viewModel(factory = factory),
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable<FocusModeRoute> {
                        FocusModeScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable<JournalRoute> {
                        JournalScreen(
                            viewModel = viewModel(factory = factory),
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable<PhilosophyRoute> {
                        PhilosophyScreen(
                            viewModel = viewModel(factory = factory),
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable<SkillTreeRoute> {
                        SkillTreeGraph(
                            nodes = listOf(
                                SkillNode("1", "Ignition", Offset(0.5f, 0.2f), unlocked = true, completed = true, pillar = "AWAKENING"),
                                SkillNode("2", "Deep Work I", Offset(0.3f, 0.4f), unlocked = true, completed = false, pillar = "TECHNE"),
                                SkillNode("3", "The Archive", Offset(0.7f, 0.4f), unlocked = true, completed = false, pillar = "HISTORIA"),
                                SkillNode("4", "Physical Fortitude", Offset(0.5f, 0.6f), unlocked = false, completed = false, pillar = "GYMNOS")
                            ),
                            edges = listOf(
                                SkillEdge("1", "2"),
                                SkillEdge("1", "3"),
                                SkillEdge("2", "4"),
                                SkillEdge("3", "4")
                            )
                        )
                    }
                }
            }
        }
    }
}
