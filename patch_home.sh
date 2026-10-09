#!/bin/bash
sed -i 's/onDailyLoginClicked: () -> Unit/onDailyLoginClicked: () -> Unit,\n    onStreakClicked: () -> Unit,\n    onEventsClicked: () -> Unit/g' app/src/main/java/com/example/ui/screens/HomeScreen.kt
sed -i 's/onDailyLoginClicked = onDailyLoginClicked/onDailyLoginClicked = onDailyLoginClicked,\n                    onStreakClicked = onStreakClicked,\n                    onEventsClicked = onEventsClicked/g' app/src/main/java/com/example/ui/screens/HomeScreen.kt
sed -i 's/fun FeatureGrid(/fun FeatureGrid(\n    onStreakClicked: () -> Unit,\n    onEventsClicked: () -> Unit,/g' app/src/main/java/com/example/ui/screens/HomeScreen.kt
sed -i 's/"DAILY LOGIN" -> onDailyLoginClicked()/"DAILY LOGIN" -> onDailyLoginClicked()\n                        "STREAK" -> onStreakClicked()\n                        "EVENTS" -> onEventsClicked()/g' app/src/main/java/com/example/ui/screens/HomeScreen.kt
