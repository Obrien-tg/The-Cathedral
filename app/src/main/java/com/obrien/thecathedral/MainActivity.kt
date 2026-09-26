package com.obrien.thecathedral

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.obrien.core.util.AlarmScheduler
import com.obrien.core.util.NotificationHelper
import com.obrien.thecathedral.data.ScheduleData
import com.obrien.thecathedral.navigation.*
import com.obrien.thecathedral.notifications.PillarReceiver
import com.obrien.thecathedral.ui.screens.*
import com.obrien.thecathedral.ui.theme.TheCathedralTheme
import com.obrien.thecathedral.ui.theme.ThemeMode
import com.obrien.thecathedral.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    private lateinit var settingsViewModel: SettingsViewModel

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            checkExactAlarmPermission()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this, "Cathedral Reminders")

        setContent {
            settingsViewModel = hiltViewModel()
            val settingsState by settingsViewModel.uiState.collectAsState()
            val themeMode = when (settingsState.theme) {
                "dark" -> ThemeMode.DARK
                "light" -> ThemeMode.LIGHT
                else -> ThemeMode.SYSTEM
            }

            TheCathedralTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    LaunchedEffect(settingsState.wakeTime) {
                        alarmScheduler.scheduleRitualAlarms(
                            pillars = ScheduleData.pillars,
                            receiverClass = PillarReceiver::class.java,
                            wakeTime = settingsState.wakeTime,
                            baseWake = java.time.LocalTime.of(7, 0)
                        )
                    }

                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute
                    ) {
                        composable<HomeRoute> {
                            HomeScreen(
                                viewModel = hiltViewModel(),
                                onViewFullSchedule = { navController.navigate(ScheduleRoute) },
                                onFocusMode = { navController.navigate(FocusModeRoute) },
                                onJournal = { navController.navigate(JournalRoute) },
                                onPhilosophy = { navController.navigate(PhilosophyRoute) },
                                onSkillTree = { navController.navigate(SkillTreeRoute) },
                                onWeeklyReview = { navController.navigate(WeeklyReviewRoute) },
                                onSettings = { navController.navigate(SettingsRoute) },
                                onWeeklyIntention = { navController.navigate(WeeklyIntentionRoute) }
                            )
                        }
                        composable<ScheduleRoute> {
                            FullScheduleScreen(
                                viewModel = hiltViewModel(),
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable<FocusModeRoute> {
                            FocusModeScreen(
                                viewModel = hiltViewModel(),
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable<JournalRoute> {
                            JournalScreen(
                                viewModel = hiltViewModel(),
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable<PhilosophyRoute> {
                            PhilosophyScreen(
                                viewModel = hiltViewModel(),
                                onBack = { navController.popBackStack() },
                                onWeeklyReview = { navController.navigate(WeeklyReviewRoute) }
                            )
                        }
                        composable<SkillTreeRoute> {
                            SkillTreeScreen(
                                viewModel = hiltViewModel(),
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable<WeeklyReviewRoute> {
                            WeeklyReviewScreen(
                                viewModel = hiltViewModel(),
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable<SettingsRoute> {
                            SettingsScreen(
                                viewModel = settingsViewModel,
                                onBack = { navController.popBackStack() },
                                onWeeklyIntention = { navController.navigate(WeeklyIntentionRoute) }
                            )
                        }
                        composable<WeeklyIntentionRoute> {
                            WeeklyIntentionScreen(
                                viewModel = hiltViewModel(),
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }

        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        checkExactAlarmPermission()
                    }
                    else -> {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            else -> {
                checkExactAlarmPermission()
            }
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                try {
                    startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:$packageName")
                    })
                } catch (_: Exception) { }
            }
        }
    }
}
