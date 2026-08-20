package com.obrien.thelantern

import android.content.Context
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.obrien.thelantern.navigation.*
import com.obrien.thelantern.ui.screens.*
import com.obrien.thelantern.ui.theme.TheLanternTheme
import com.obrien.core.util.AlarmScheduler
import com.obrien.core.util.NotificationHelper
import com.obrien.thelantern.notifications.PillarReceiver
import com.obrien.thelantern.data.ScheduleData
import com.obrien.thelantern.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    private val timeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // System time changed; rescheduling is handled by the app's internal logic 
            // or can be triggered here if we inject a direct preference reader.
            // For now, we rely on the app's state persistence.
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this, "Lantern Reminders")

        val filter = android.content.IntentFilter().apply {
            addAction(android.content.Intent.ACTION_TIME_CHANGED)
            addAction(android.content.Intent.ACTION_TIMEZONE_CHANGED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(timeChangeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(timeChangeReceiver, filter)
        }

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
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                try {
                    startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                } catch (_: Exception) { }
            }
        }

        setContent {
            TheLanternTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onSplashFinished = { showSplash = false })
                } else {
                    val navController = rememberNavController()
                    
                    val settingsViewModel: SettingsViewModel = hiltViewModel()
                    val settingsState by settingsViewModel.uiState.collectAsState()
                    
                    LaunchedEffect(settingsState.wakeTime) {
                        alarmScheduler.scheduleRitualAlarms(
                            pillars = ScheduleData.pillars,
                            receiverClass = PillarReceiver::class.java,
                            wakeTime = settingsState.wakeTime,
                            baseWake = java.time.LocalTime.of(6, 30)
                        )
                    }

                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute
                    ) {
                        composable<HomeRoute>(
                            deepLinks = listOf(navDeepLink { uriPattern = "lantern://home" })
                        ) {
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
                        composable<ScheduleRoute>(
                            deepLinks = listOf(navDeepLink { uriPattern = "lantern://schedule" })
                        ) {
                            FullScheduleScreen(
                                viewModel = hiltViewModel(),
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable<FocusModeRoute>(
                            deepLinks = listOf(navDeepLink { uriPattern = "lantern://focus" })
                        ) {
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
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timeChangeReceiver)
    }
}
