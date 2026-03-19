package com.kaizen.skywear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kaizen.skywear.ui.theme.SkyWearTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkyWearTheme {

            }
        }
    }
}

@Composable
fun ThemePreviewScreen() {
    var isDark by remember { mutableStateOf(false) }

    SkyWearTheme(darkTheme = isDark) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement, spacedBy(20.dp)
            ) {
                // DarkMode toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SkyWear Theme Preview",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Switch(
                        checked = isDark,
                        onCheckedChange = { isDark = it }
                    )
                }

                // Dual City Card
                // KR Card
                // JP Card
                // 온도 차이 뱃지
                // 8단계 온도 팔레트
                // Typography 샘플
            }
        }
    }
}

// Android Studio 미리보기
@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewLight() {
    SkyWearTheme(darkTheme = false) { ThemePreviewScreen() }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1C1E, name = "Dark Mode")
@Composable
fun PreviewDark() {
    SkyWearTheme(darkTheme = true) { ThemePreviewScreen() }
}