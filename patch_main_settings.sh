#!/bin/bash
sed -i 's/onDailyLoginClicked = {/onSettingsClicked = { navController.navigate("settings") },\n                onDailyLoginClicked = {/g' app/src/main/java/com/example/MainActivity.kt
