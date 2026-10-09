#!/bin/bash
sed -i 's/composable("settings") { PlaceholderScreen("Settings (Coming Soon)") }/composable("settings") { com.example.ui.screens.SettingsScreen(viewModel = viewModel, onBackClicked = { navController.popBackStack() }) }/g' app/src/main/java/com/example/MainActivity.kt
