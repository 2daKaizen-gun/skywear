package com.kaizen.skywear

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.kaizen.skywear.ui.theme.SkyWearTheme
import org.junit.Rule
import org.junit.Test

// Compose Test Rule 기반 UI 테스트

class ThemePreviewScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // 기본 UI 요소 표시 여부
    @Test
    fun `ThemePreviewScreen이 정상적으로 렌더링됨`() {
        composeTestRule.setContent {
            SkyWearTheme {
                ThemePreviewScreen()
            }
        }

        // 타이틀 표시 확인
        composeTestRule
            .onNodeWithText("SkyWear Theme Preview")
            .assertIsDisplayed()
    }

    @Test
    fun `서울 카드 표시됨`() {
        composeTestRule.setContent {
            SkyWearTheme {
                ThemePreviewScreen()
            }
        }

        composeTestRule
            .onNodeWithText("-2°")
            .assertIsDisplayed()
    }


    // 다크 모드 스위치 동작 검증


    // Typography 샘플 표시 여부


}