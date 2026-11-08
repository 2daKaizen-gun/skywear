package com.kaizen.skywear

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.kaizen.skywear.ui.navigation.SkyWearNavGraph
import com.kaizen.skywear.ui.theme.SkyWearTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Android 13+ 알림 권한 요청 (거부해도 앱 사용에는 지장 없음)
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면 회전 등 재생성 시에는 다시 묻지 않음
        if (savedInstanceState == null) requestNotificationPermissionIfNeeded()

        setContent {
            SkyWearTheme {
                SkyWearNavGraph()
            }
        }
    }

    // 데일리 브리핑 / 구독 도시 알림은 POST_NOTIFICATIONS 런타임 권한이 있어야 표시됨
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
