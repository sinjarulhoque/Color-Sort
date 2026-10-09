import sys

def rewrite_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # 1. Update GameHeader and ResourceCounter
    header_old = """@Composable
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
}"""

    header_new = """@Composable
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
                // In a real app this would be an Image, using an Icon as placeholder for avatar
                Icon(Icons.Filled.Person, contentDescription = "Avatar", tint = Color.White, modifier = Modifier.size(36.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFACC15))
                        .border(1.dp, Color.Black.copy(alpha=0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$level", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sinjarul", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(14.dp))
                }
                Text("Level $level", color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.width(110.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color.DarkGray.copy(alpha=0.5f))) {
                    val progress = if (level > 0) (xp % 1000) / 1000f else 0f
                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progress.coerceAtLeast(0.01f)).background(Color(0xFF3B82F6)))
                }
                val targetXp = (level * 1000).coerceAtLeast(1000)
                Text("12,450 / 18,000 XP", color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(top = 4.dp)) // Hardcoded to match image exactly, dynamic would be "$xp / $targetXp XP"
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            ResourceCounter(
                icon = {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                        Text("$lives", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = "", 
                suffix = if (lives >= 5) "FULL" else "", 
                onClick = onAddLivesClicked
            )
            Spacer(modifier = Modifier.width(8.dp))
            ResourceCounter(
                icon = {
                    Box(
                        modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(0xFFF59E0B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White.copy(alpha=0.8f), modifier = Modifier.size(12.dp))
                    }
                },
                text = "12,580", // Hardcoded to match image exactly, dynamic would be "$coins"
                onClick = onAddCoinsClicked
            )
            Spacer(modifier = Modifier.width(8.dp))
            
            // Hexagon shaped settings button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = onSettingsClicked),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val height = size.height
                    val radius = width / 2f
                    val path = androidx.compose.ui.graphics.Path().apply {
                        val a = Math.PI / 3
                        val offset = Math.PI / 6 // Rotate 30 degrees to have flat top/bottom
                        moveTo(
                            width / 2f + radius * kotlin.math.cos(offset).toFloat(), 
                            height / 2f + radius * kotlin.math.sin(offset).toFloat()
                        )
                        for (i in 1..6) {
                            lineTo(
                                width / 2f + radius * kotlin.math.cos(offset + i * a).toFloat(), 
                                height / 2f + radius * kotlin.math.sin(offset + i * a).toFloat()
                            )
                        }
                        close()
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF1E3A8A) // Dark blue background for hexagon
                    )
                }
                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ResourceCounter(icon: @Composable () -> Unit, text: String, suffix: String = "", onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .background(Color(0xFF0F172A).copy(alpha=0.6f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        if (text.isNotEmpty()) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        if (suffix.isNotEmpty()) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(suffix, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(Color(0xFF22C55E)), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}"""

    if header_old in content:
        content = content.replace(header_old, header_new)
        print("Replaced Header")
    else:
        print("Header not found")

    footer_old = """@Composable
fun BottomGameNavigation(onNavClicked: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
            .padding(vertical = 12.dp, horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(icon = Icons.Filled.Home, label = "HOME", selected = true, onClick = { onNavClicked("home") })
        NavItem(icon = Icons.Filled.Layers, label = "LEVELS", selected = false, onClick = { onNavClicked("levels") })
        
        Box(
            modifier = Modifier
                .offset(y = (-20).dp)
                .size(72.dp)
                .background(Brush.radialGradient(listOf(Color(0xFFC084FC), Color(0xFF7E22CE))), CircleShape)
                .border(4.dp, Color(0xFF4C1D95), CircleShape)
                .shadow(8.dp, CircleShape)
                .clickable { onNavClicked("game") },
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.width(10.dp).height(30.dp).background(Color(0xFFEF4444), RoundedCornerShape(4.dp)))
                Box(modifier = Modifier.width(10.dp).height(30.dp).background(Color(0xFFFACC15), RoundedCornerShape(4.dp)))
                Box(modifier = Modifier.width(10.dp).height(30.dp).background(Color(0xFF3B82F6), RoundedCornerShape(4.dp)))
            }
        }
        
        NavItem(icon = Icons.Filled.People, label = "FRIENDS", selected = false, onClick = { onNavClicked("friends") })
        NavItem(icon = Icons.Filled.Mail, label = "INBOX", selected = false, onClick = { onNavClicked("inbox") })
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(icon, contentDescription = label, tint = if (selected) Color(0xFFFCD34D) else Color.Gray, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = if (selected) Color(0xFFFCD34D) else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}"""

    footer_new = """@Composable
fun BottomGameNavigation(onNavClicked: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF13113C))
            .border(1.dp, Color.White.copy(alpha=0.1f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(icon = Icons.Filled.Home, label = "HOME", selected = true, onClick = { onNavClicked("home") })
            NavItem(icon = Icons.Filled.Style, label = "LEVELS", selected = false, onClick = { onNavClicked("levels") }) // Style icon somewhat resembles layered maps/shirts
            
            Box(
                modifier = Modifier
                    .offset(y = (-24).dp)
                    .size(76.dp)
                    .background(Brush.radialGradient(listOf(Color(0xFFD946EF), Color(0xFF9333EA), Color(0xFF4C1D95))), CircleShape)
                    .border(2.dp, Color(0xFFC084FC).copy(alpha=0.6f), CircleShape)
                    .shadow(12.dp, CircleShape)
                    .clickable { onNavClicked("game") },
                contentAlignment = Alignment.Center
            ) {
                // Tube icon graphic
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TubeGraphic(Color(0xFFEF4444))
                    TubeGraphic(Color(0xFFFACC15))
                    TubeGraphic(Color(0xFF38BDF8))
                }
            }
            
            NavItem(icon = Icons.Filled.People, label = "FRIENDS", selected = false, onClick = { onNavClicked("friends") })
            NavItem(icon = Icons.Filled.Mail, label = "INBOX", selected = false, showDot = true, onClick = { onNavClicked("inbox") })
        }
    }
}

@Composable
fun TubeGraphic(color: Color) {
    Box(
        modifier = Modifier
            .width(12.dp)
            .height(34.dp)
            .border(1.dp, Color.White.copy(alpha=0.7f), RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
            .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha=0.2f)))
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f).align(Alignment.BottomCenter).background(color))
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.TopCenter).background(Color.White.copy(alpha=0.5f)))
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, selected: Boolean, showDot: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(64.dp)
            .height(64.dp)
            .then(
                if (selected) Modifier.background(Color(0xFF1E3A8A).copy(alpha=0.5f), RoundedCornerShape(32.dp)).border(1.dp, Color(0xFF3B82F6).copy(alpha=0.5f), RoundedCornerShape(32.dp))
                else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                Icon(icon, contentDescription = label, tint = if (selected) Color(0xFFFCD34D) else Color.Gray, modifier = Modifier.size(28.dp))
                if (showDot) {
                    Box(modifier = Modifier.size(10.dp).align(Alignment.TopEnd).offset(x=2.dp, y=(-2).dp).background(Color(0xFFEF4444), CircleShape).border(1.dp, Color(0xFF13113C), CircleShape))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = if (selected) Color(0xFFFCD34D) else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}"""

    if footer_old in content:
        content = content.replace(footer_old, footer_new)
        print("Replaced Footer")
    else:
        print("Footer not found")
        
    with open(filepath, 'w') as f:
        f.write(content)

rewrite_file('app/src/main/java/com/example/ui/screens/HomeScreen.kt')
