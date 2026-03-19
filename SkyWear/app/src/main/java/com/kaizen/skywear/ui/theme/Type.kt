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

// SkyWear Typography Scale(Material 3 기반)

val SkyWearTypography = Typography(

    // Display
    // 사용: 메인 온도 숫자(ex: -2°C, 10°C)
    displayLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    // 사용: Dual-City 카드 내 대형으로 온도 표시함
    displayMedium = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    // 사용: 온도 차이 뱃지 (+12°C)
    displaySmall = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // Headline
    // 사용: 도시명(Seoul, Tokyo)
    headlineLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    // 사용: 섹션 헤더 (코디 추천, 여행 체크리스트)
    headlineMedium = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    // 사용: 카드 제목
    headlineSmall = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    //Title
    // 사용: 앱바 타이틀 "SkyWear"
    titleLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // 사용: 코디 추천 아이템명 (예: 롱패딩 + 목도리)
    titleMedium = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    // 사용: 날씨 상태 텍스트 (맑음, 흐림, 비)
    titleSmall = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    //body
    // 사용: 여행자 체크리스트 아이템 텍스트
    bodyLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5sp
    ),
    // 사용: 일반 설명 텍스트, 알림 내용
    bodyMedium = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    // 사용: 습도, 풍속 등의 자세한 수치
    bodySmall = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4sp
    ),

    //label
    // 사용: 버튼 텍스트
    labelLarge = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    // 사용: 뱃지, 태그(ex KR, JR, 한파주의보 등..)
    labelMedium = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // 사용: 타임스탬프, 최소 보조 정보들
    labelSmall = TextStyle(
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)