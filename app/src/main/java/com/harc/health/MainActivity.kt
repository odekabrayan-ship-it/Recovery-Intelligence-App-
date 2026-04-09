package com.harc.health

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.harc.health.logic.RecoveryNotificationManager
import com.harc.health.logic.SessionManager
import com.harc.health.ui.Screen
import com.harc.health.ui.auth.ProfileScreen
import com.harc.health.ui.auth.LockScreen
import com.harc.health.ui.coach.CoachScreen
import com.harc.health.ui.home.HomeScreen
import com.harc.health.ui.insights.InsightsScreen
import com.harc.health.ui.intake.IntakeLoggingScreen
import com.harc.health.ui.recovery.RecoveryPlanScreen
import com.harc.health.ui.theme.HARCHealthTheme
import com.harc.health.ui.vitalis.VitalisScreen
import com.harc.health.ui.therapeutic.TherapeuticScreen
import com.harc.health.ui.therapeutic.TherapeuticPlayerScreen
import com.harc.health.ui.therapeutic.TherapeuticSession
import com.harc.health.viewmodel.MainViewModel
import com.harc.health.viewmodel.ProfileViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        RecoveryNotificationManager.createNotificationChannel(this)
        
        // Request notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        setContent {
            HARCHealthTheme {
                MainContent(intent)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@Composable
fun MainContent(intent: Intent) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val userProfile by profileViewModel.userProfile.collectAsState()
    val sessionManager = remember { SessionManager(navController.context) }
    
    var activeTherapeuticSession by remember { mutableStateOf<TherapeuticSession?>(null) }

    LaunchedEffect(userProfile?.language) {
        userProfile?.language?.let { lang ->
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
            if (AppCompatDelegate.getApplicationLocales() != appLocale) {
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
        }
    }

    val items = listOf(
        Screen.Home,
        Screen.Recovery,
        Screen.Insights,
        Screen.Coach
    )

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MainViewModel.UiEvent.NavigateToRecovery -> {
                    navController.navigate(Screen.Recovery.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                else -> {}
            }
        }
    }

    val showBars = currentDestination?.route != Screen.Lock.route && 
                   currentDestination?.route != Screen.Intake.route && 
                   currentDestination?.route != Screen.Vitalis.route &&
                   currentDestination?.route != Screen.Therapeutic.route &&
                   activeTherapeuticSession == null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBars) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    items.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = null 
                                ) 
                            },
                            label = { 
                                Text(
                                    text = stringResource(screen.label),
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            selected = isSelected,
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
        },
        floatingActionButton = {
            if (currentDestination?.route == Screen.Home.route) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SmallFloatingActionButton(
                        onClick = { navController.navigate(Screen.Therapeutic.route) },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Icon(Icons.Default.Spa, contentDescription = "Therapeutic")
                    }

                    SmallFloatingActionButton(
                        onClick = { navController.navigate(Screen.Vitalis.route) },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Icon(Icons.Default.AutoGraph, contentDescription = "Vitalis Engine")
                    }
                    
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.Intake.route) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Log Intake")
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = if (sessionManager.isProtected()) Screen.Lock.route else Screen.Home.route,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { fadeIn(tween(400)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) },
                exitTransition = { fadeOut(tween(400)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) },
                popEnterTransition = { fadeIn(tween(400)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) },
                popExitTransition = { fadeOut(tween(400)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) }
            ) {
                composable(Screen.Lock.route) {
                    LockScreen(onAuthenticated = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Lock.route) { inclusive = true }
                        }
                    })
                }
                composable(Screen.Home.route) { 
                    HomeScreen(
                        viewModel = viewModel, 
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onNavigateToTherapeutic = { navController.navigate(Screen.Therapeutic.route) },
                        onNavigateToVitalis = { navController.navigate(Screen.Vitalis.route) }
                    ) 
                }
                composable(Screen.Recovery.route) { RecoveryPlanScreen(viewModel) }
                composable(Screen.Vitalis.route) { 
                    VitalisScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    ) 
                }
                composable(Screen.Insights.route) { InsightsScreen(viewModel) }
                composable(Screen.Coach.route) { CoachScreen(viewModel) }
                composable(Screen.Intake.route) { 
                    IntakeLoggingScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onBack = { navController.popBackStack() },
                        onLogout = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        viewModel = profileViewModel
                    )
                }
                composable(Screen.Therapeutic.route) {
                    TherapeuticScreen(
                        onBack = { navController.popBackStack() },
                        onSessionClick = { session -> activeTherapeuticSession = session }
                    )
                }
            }

            AnimatedVisibility(
                visible = activeTherapeuticSession != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                activeTherapeuticSession?.let { session ->
                    TherapeuticPlayerScreen(
                        session = session,
                        onBack = { activeTherapeuticSession = null }
                    )
                }
            }
        }
    }
}
