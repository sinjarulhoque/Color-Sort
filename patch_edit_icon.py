import sys

def rewrite_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    old_edit = """Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sinjarul", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(14.dp))
                }"""

    new_edit = """Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sinjarul", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(0xFF2563EB)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(10.dp))
                    }
                }"""

    if old_edit in content:
        content = content.replace(old_edit, new_edit)
        print("Replaced Edit icon")
    else:
        print("Edit icon not found")
        
    with open(filepath, 'w') as f:
        f.write(content)

rewrite_file('app/src/main/java/com/example/ui/screens/HomeScreen.kt')
