package com.kaizen.skywear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kaizen.skywear.ui.navigation.SkyWearNavGraph
import com.kaizen.skywear.ui.theme.SkyWearTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SkyWearTheme {
                SkyWearNavGraph()
            }
        }
    }
}
