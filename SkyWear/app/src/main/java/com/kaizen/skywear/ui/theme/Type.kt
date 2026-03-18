package com.kaizen.skywear.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Font Family

// TODO: Pretendard 폰트 적용 시 아래 주석 해제 후 FontFamily.Default 제거
// 1. https://github.com/orioncactus/pretendard 에서 .ttf 다운로드
// 2. app/src/main/res/font/ 폴더에 파일 추가
// 3. 아래 PretendardFontFamily 주석 해제, FontFamily.Default 제거
//
// val PretendardFontFamily = FontFamily(
//     Font(resId = com.skywear.R.font.pretendard_light,    weight = FontWeight.Light),
//     Font(resId = com.skywear.R.font.pretendard_regular,  weight = FontWeight.Normal),
//     Font(resId = com.skywear.R.font.pretendard_medium,   weight = FontWeight.Medium),
//     Font(resId = com.skywear.R.font.pretendard_semibold, weight = FontWeight.SemiBold),
//     Font(resId = com.skywear.R.font.pretendard_bold,     weight = FontWeight.Bold),
// )

val PretendardFontFamily = FontFamily.Default  // 임시: 빌드 에러 방지

