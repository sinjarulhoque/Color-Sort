import sys

def rewrite_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    hex_old = """            // Settings button
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
            }"""

    hex_new = """            // Settings button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E3A8A))
                    .clickable(onClick = onSettingsClicked),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(20.dp))
            }"""

    if hex_old in content:
        content = content.replace(hex_old, hex_new)
        print("Replaced hex button")
    else:
        print("Hex button not found")

    with open(filepath, 'w') as f:
        f.write(content)

rewrite_file('app/src/main/java/com/example/ui/screens/HomeScreen.kt')
