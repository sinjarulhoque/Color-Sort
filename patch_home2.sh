#!/bin/bash
sed -i '353,354d' app/src/main/java/com/example/ui/screens/HomeScreen.kt
sed -i 's/onDailyLoginClicked = onDailyLoginClicked/onDailyLoginClicked = onDailyLoginClicked,\n                    onStreakClicked = onStreakClicked,\n                    onEventsClicked = onEventsClicked/g' app/src/main/java/com/example/ui/screens/HomeScreen.kt
