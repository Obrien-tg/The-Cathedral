package com.obrien.thecathedral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.obrien.thecathedral.navigation.*
import com.obrien.thecathedral.ui.screens.*
import com.obrien.thecathedral.ui.skilltree.*
import com.obrien.thecathedral.ui.theme.TheCathedralTheme
import com.obrien.thecathedral.util.AlarmScheduler
import com.obrien.thecathedral.util.NotificationHelper
import com.obrien.thecathedral.viewmodel.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)
        alarmScheduler.scheduleRitualAlarms()

        splashScreen.setKeepOnScreenCondition { false }

        setContent {
            TheCathedralTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onSplashFinished = { showSplash = false })
                } else {
                    val navController = rememberNavController()
                    val viewModel: ScheduleViewModel = hiltViewModel()

                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute
                    ) {
                        composable<HomeRoute> {
                            HomeScreen(
                                viewModel = viewModel,
                                onViewFullSchedule = { navController.navigate(ScheduleRoute) },
                                onFocusMode = { navController.navigate(FocusModeRoute) },
                                onJournal = { navController.navigate(JournalRoute) },
                                onPhilosophy = { navController.navigate(PhilosophyRoute) },
                                onSkillTree = { navController.navigate(SkillTreeRoute) }
                            )
                        }
                        composable<ScheduleRoute> {
                            FullScheduleScreen(
                                viewModel = viewModel,
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
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable<PhilosophyRoute> {
                            PhilosophyScreen(
                                viewModel = viewModel,
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
}
