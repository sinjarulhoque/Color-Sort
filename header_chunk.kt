        }
    }
}

@Composable
fun GameHeader(
    level: Int, 
    xp: Int, 
    coins: Int, 
    lives: Int, 
    onProfileClicked: () -> Unit,
    onAddCoinsClicked: () -> Unit,
    onAddLivesClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).clickable { onProfileClicked() }) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF59E0B))
                    .border(2.dp, Color(0xFFFCD34D), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = "Avatar", tint = Color.White, modifier = Modifier.size(36.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB))
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$level", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sinjarul", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🇮🇳", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                }
                Text("Level $level", color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.width(110.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color.DarkGray.copy(alpha=0.5f))) {
                    val progress = if (level > 0) (xp % 1000) / 1000f else 0f
                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progress.coerceAtLeast(0.01f)).background(Color(0xFF3B82F6)))
                }
                val targetXp = (level * 1000).coerceAtLeast(1000)
                Text("$xp / $targetXp XP", color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            ResourceCounter(icon = Icons.Filled.Favorite, iconTint = Color(0xFFEF4444), text = "$lives", suffix = if (lives >= 5) "FULL" else "", onClick = onAddLivesClicked)
            Spacer(modifier = Modifier.width(8.dp))
            ResourceCounter(icon = Icons.Filled.Stars, iconTint = Color(0xFFF59E0B), text = "$coins", onClick = onAddCoinsClicked)
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onSettingsClicked, modifier = Modifier.size(36.dp).background(Color.Black.copy(alpha=0.4f), RoundedCornerShape(8.dp))) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
            }
        }
    }
}

@Composable
fun ResourceCounter(icon: ImageVector, iconTint: Color, text: String, suffix: String = "", onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .background(Color.Black.copy(alpha=0.4f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        if (suffix.isNotEmpty()) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(suffix, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(Color(0xFF22C55E)), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun SideActionButton(icon: ImageVector, title: String, time: String, bgColor: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
