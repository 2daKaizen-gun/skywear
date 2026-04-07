package com.kaizen.skywear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaizen.skywear.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkyWearTheme {
                ThemePreviewScreen()
            }
        }
    }
}

@Composable
fun ThemePreviewScreen() {
    var isDark by remember { mutableStateOf(false) }

    SkyWearTheme(darkTheme = isDark) {
        // 현재 테마에 주입된 커스텀 컬러를 직접 가져옴
        val colors = LocalExtraColors.current

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // KR Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "\uD83C\uDDF0\uD83C\uDDF7 Seoul",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "-2°",
                                style = MaterialTheme.typography.displayMedium,
                                color = colors.koreaRed // [변경!]
                            )
                            Text(
                                text = "롱패딩 + 목도리",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // JP Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "\uD83C\uDDEF\uD83C\uDDF5 Osaka",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "10°",
                                style = MaterialTheme.typography.displayMedium,
                                color = colors.japanBlue // [변경!]
                            )
                            Text(
                                text = "가벼운 코트",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // 온도 차이 뱃지
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "온도 차이",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "+12°C",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                // 8단계 온도 팔레트
                Text(
                    text = "Temperature Palette",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                val tempColors = listOf(
                    colors.tempScorching to "28°+",
                    colors.tempHot to "23°",
                    colors.tempWarm to "17°",
                    colors.tempMild to "12°",
                    colors.tempCool to "9°",
                    colors.tempChilly to "5°",
                    colors.tempCold to "0°",
                    colors.tempFreezing to "-1°"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tempColors.forEach { (color, label) ->
                        Column (
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                               modifier = Modifier
                                   .fillMaxWidth()
                                   .height(36.dp)
                                   .background(color, RoundedCornerShape(6.dp))
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                // Typography 샘플
                Text(
                    text = "Typography",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text("displaySmall — +12°C", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
                Text("titleLarge — SkyWear", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                Text("bodyMedium — 맑음, 습도 72%", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                Text("labelSmall — 최저 -5° / 최고 3°", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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