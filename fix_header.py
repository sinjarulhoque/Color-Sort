import sys

def rewrite_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    old_xp = """Text("12,450 / 18,000 XP", color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(top = 4.dp)) // Hardcoded to match image exactly, dynamic would be "$xp / $targetXp XP\""""
    new_xp = """Text("$xp / $targetXp XP", color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(top = 4.dp))"""
    
    old_coins = """text = "12,580", // Hardcoded to match image exactly, dynamic would be "$coins\""""
    new_coins = """text = "$coins","""

    content = content.replace(old_xp, new_xp).replace(old_coins, new_coins)
    
    with open(filepath, 'w') as f:
        f.write(content)

rewrite_file('app/src/main/java/com/example/ui/screens/HomeScreen.kt')
