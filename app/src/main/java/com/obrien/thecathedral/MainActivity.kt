package com.obrien.thecathedral

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import com.obrien.thecathedral.model.SkillTreeData
import com.obrien.thecathedral.navigation.*
import com.obrien.thecathedral.ui.screens.*
import com.obrien.thecathedral.ui.skilltree.*
import com.obrien.thecathedral.ui.theme.TheCathedralTheme
import com.obrien.thecathedral.util.AlarmScheduler
import com.obrien.thecathedral.util.NotificationHelper
import com.obrien.thecathedral.viewmodel.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Request exact alarm permission for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                try {
                    startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                } catch (_: Exception) { }
            }
        }

        setContent {
            TheCathedralTheme {
                val navController = rememberNavController()
                val viewModel: ScheduleViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()
                
                // Reschedule alarms whenever wake time changes
                LaunchedEffect(uiState.wakeTime) {
                    alarmScheduler.scheduleRitualAlarms(uiState.wakeTime)
                }

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
                            viewModel = viewModel,
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
                        val progressMap = uiState.skillProgress.associateBy { it.nodeId }

                        SkillTreeGraph(
                            nodes = SkillTreeData.nodes.map { node ->
                                val prog = progressMap[node.id]
                                SkillNode(
                                    id = node.id,
                                    name = node.title,
                                    position = when (node.id) {
                                        "1" -> Offset(0.5f, 0.15f)
                                        "2" -> Offset(0.25f, 0.4f)
                                        "3" -> Offset(0.75f, 0.4f)
                                        "4" -> Offset(0.5f, 0.65f)
                                        "5" -> Offset(0.5f, 0.85f)
                                        else -> Offset(0.5f, 0.5f)
                                    },
                                    unlocked = prog?.unlocked ?: false,
                                    completed = prog?.completed ?: false,
                                    pillar = node.pillar,
                                    progress = prog?.progress ?: 0f
                                )
                            },
                            edges = SkillTreeData.edges.map { SkillEdge(it.from, it.to) }
                        )
                    }
                }
            }
        }
    }
}
