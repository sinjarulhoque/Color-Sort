package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.AppPrefs
import com.example.ui.theme.DarkColors
import com.example.ui.theme.LightColors
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.LocalDarkMode
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.GameViewModelFactory
import com.example.ui.screens.AuthScreen
import com.example.viewmodel.AuthViewModel
import com.example.ads.AdService
import com.example.data.FirestoreRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as ColorSortApplication
        
        setContent {
            val darkState = remember { mutableStateOf(AppPrefs.isDark(app)) }
            CompositionLocalProvider(
                LocalDarkMode provides darkState,
                LocalAppColors provides if (darkState.value) DarkColors else LightColors
            ) {
                MyApplicationTheme(darkTheme = darkState.value, dynamicColor = false) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ColorSortApp(app)
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSortApp(app: ColorSortApplication) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        authViewModel.handleGoogleSignInResult(result)
    }
    if (authState.user == null) {
        AuthScreen(viewModel = authViewModel, onGoogleSignIn = { googleLauncher.launch(authViewModel.googleSignInIntent(app)) })
        return
    }

    val navController = rememberNavController()
    val viewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(app.repository, app.soundManager, app.firestoreRepository)
    )

    LaunchedEffect(Unit) {
        if (AppPrefs.isMusic(app)) app.musicManager.start(app)
    }
    DisposableEffect(Unit) {
        onDispose { app.musicManager.stop() }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onPlayClicked = { levelId ->
                    navController.navigate("game/$levelId")
                },
                onThemesClicked = {
                    navController.navigate("themes")
                },
                onDailyClicked = {
                    navController.navigate("daily_challenge")
                },
                onShopClicked = {
                    navController.navigate("shop")
                },
                onLeaderboardClicked = {
                    navController.navigate("leaderboard")
                },
                onProfileClicked = {
                    navController.navigate("profile")
                },
                onAchievementsClicked = {
                    navController.navigate("achievements")
                },
                onSettingsClicked = { navController.navigate("settings") },
                onStreakClicked = { navController.navigate("streak") },
                onEventsClicked = { navController.navigate("events") },
                onDailyLoginClicked = {
                    navController.navigate("daily_login")
                },
                onBottomNavClicked = { route ->
                    when (route) {
                        "home" -> navController.navigate("home") { popUpTo("home") { inclusive = true } }
                        "levels" -> navController.navigate("levels")
                        "game" -> navController.navigate("game/${viewModel.playerStats.value.highestUnlockedLevel}")
                        "friends" -> navController.navigate("friends")
                        "inbox" -> navController.navigate("inbox")
                    }
                },
                user = authState.user
                ,onFreeCoinsClicked = { navController.navigate("free_coins") }
            )
        }
        
        composable("shop") {
            ShopScreen(viewModel = viewModel, onBackClicked = { navController.popBackStack() })
        }
        composable("free_coins") { com.example.ui.screens.FreeCoinsScreen(viewModel = viewModel, firestoreRepository = app.firestoreRepository, adService = app.adService, onBackClicked = { navController.popBackStack() }) }
        composable("leaderboard") { com.example.ui.screens.LeaderboardScreen(viewModel = viewModel, firestoreRepository = app.firestoreRepository, onBackClicked = { navController.popBackStack() }) }
        composable("profile") { com.example.ui.screens.ProfileScreen(viewModel = viewModel, firestoreRepository = app.firestoreRepository, onBackClicked = { navController.popBackStack() }, user = authState.user) }
        composable("levels") { com.example.ui.screens.LevelsScreen(viewModel = viewModel, onBackClicked = { navController.popBackStack() }, onLevelSelected = { level -> navController.navigate("game/$level") }) }
        composable("friends") { com.example.ui.screens.FriendsScreen(viewModel = viewModel, onBackClicked = { navController.popBackStack() }) }
        composable("inbox") { com.example.ui.screens.InboxScreen(viewModel = viewModel, firestoreRepository = app.firestoreRepository, user = authState.user, onBackClicked = { navController.popBackStack() }) }
        composable("settings") { com.example.ui.screens.SettingsScreen(viewModel = viewModel, onBackClicked = { navController.popBackStack() }, onLogout = authViewModel::signOut, musicManager = app.musicManager) }
        composable("achievements") { com.example.ui.screens.AchievementsScreen(viewModel = viewModel, onBackClicked = { navController.popBackStack() }) }
        composable("daily_login") { com.example.ui.screens.DailyLoginScreen(viewModel = viewModel, firestoreRepository = app.firestoreRepository, adService = app.adService, user = authState.user, onBackClicked = { navController.popBackStack() }) }
        composable("streak") { com.example.ui.screens.StreakScreen(viewModel = viewModel, onBackClicked = { navController.popBackStack() }) }
        composable("events") { com.example.ui.screens.EventsScreen(viewModel = viewModel, onBackClicked = { navController.popBackStack() }) }
        
        composable("daily_challenge") {
            com.example.ui.screens.DailyChallengeScreen(
                viewModel = viewModel,
                onPlay = { dateString ->
                    navController.navigate("daily_game/$dateString")
                },
                onCalendar = {
                    navController.navigate("calendar")
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("calendar") {
            com.example.ui.screens.CalendarScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("themes") {
            com.example.ui.screens.ThemeScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("game/{levelId}") { backStackEntry ->
            val levelId = backStackEntry.arguments?.getString("levelId")?.toIntOrNull() ?: 1
            GameScreen(
                viewModel = viewModel,
                levelId = levelId,
                dailyDate = null,
                onNextLevel = { nextId ->
                    navController.navigate("game/$nextId") {
                        popUpTo("home")
                    }
                },
                onBack = { navController.popBackStack("home", false) },
                adService = app.adService,
                firestoreRepository = app.firestoreRepository
            )
        }
        
        composable("daily_game/{dateString}") { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("dateString") ?: ""
            GameScreen(
                viewModel = viewModel,
                levelId = 9999,
                dailyDate = dateString,
                onNextLevel = { 
                    navController.popBackStack("daily_challenge", false)
                },
                onBack = { navController.popBackStack() },
                adService = app.adService,
                firestoreRepository = app.firestoreRepository
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)), contentAlignment = Alignment.Center) {
        Text(title, color = Color.White, style = MaterialTheme.typography.headlineMedium)
    }
}
