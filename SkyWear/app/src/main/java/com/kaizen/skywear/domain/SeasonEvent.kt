package com.kaizen.skywear.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class SeasonEvent(
    val titleKo: String,
    val titleEn: String,
    val titleJa: String,
    val city: String,
    val month: Int,
    val day: Int,
    val avgTempKo: String,
    val avgTempEn: String,
    val avgTempJa: String,
    val emoji: String,
    val tipKo: String,
    val tipEn: String,
    val tipJa: String,
    val colorTag: String   // "green" / "blue" / "amber" / "coral"
) {
    // D-day 계산
    fun dDay(): Long {
        val today = LocalDate.now()
        var target = LocalDate.of(today.year, month, day)
        if (target.isBefore(today)) target = target.plusYears(1)
        return ChronoUnit.DAYS.between(today, target)
    }

    fun isNow(): Boolean = dDay() <= 14
}

// 한·일 계절별 주요 이벤트
val SEASON_EVENTS = listOf(
    SeasonEvent(
        titleKo = "교토·도쿄 벚꽃 시즌",
        titleEn = "Kyoto & Tokyo Cherry Blossom",
        titleJa = "京都・東京 桜シーズン",
        city = "Kyoto / Tokyo", month = 3, day = 25,
        avgTempKo = "평균 10°C · 가벼운 코트",
        avgTempEn = "Avg 10°C · Light coat",
        avgTempJa = "平均10°C・軽いコート",
        emoji = "🌸",
        tipKo = "서울보다 약간 따뜻해요. 가벼운 코트 하나면 충분!",
        tipEn = "Slightly warmer than Seoul. A light coat is enough!",
        tipJa = "ソウルより少し暖かい。軽いコートで十分！",
        colorTag = "coral"
    ),
    SeasonEvent(
        titleKo = "교토 신록 시즌",
        titleEn = "Kyoto Fresh Greenery",
        titleJa = "京都 新緑シーズン",
        city = "Kyoto", month = 5, day = 10,
        avgTempKo = "평균 20°C · 가디건",
        avgTempEn = "Avg 20°C · Cardigan",
        avgTempJa = "平均20°C・カーディガン",
        emoji = "🌿",
        tipKo = "서울과 기온 비슷. 관광객 적어 여행하기 딱 좋아요!",
        tipEn = "Similar to Seoul temps. Great time with fewer tourists!",
        tipJa = "ソウルと気温が似ています。観光客が少なく最適！",
        colorTag = "green"
    ),
    SeasonEvent(
        titleKo = "오사카·도쿄 장마 시작",
        titleEn = "Osaka & Tokyo Rainy Season",
        titleJa = "大阪・東京 梅雨入り",
        city = "Osaka / Tokyo", month = 6, day = 8,
        avgTempKo = "평균 22°C · 우산 필수",
        avgTempEn = "Avg 22°C · Umbrella essential",
        avgTempJa = "平均22°C・傘必須",
        emoji = "☔",
        tipKo = "우산·우비 필수! 실내 관광 위주로 일정 짜세요.",
        tipEn = "Bring umbrella & raincoat! Plan indoor activities.",
        tipJa = "傘・レインコート必須！屋内観光中心のプランを。",
        colorTag = "blue"
    ),
    SeasonEvent(
        titleKo = "삿포로 여름 맥주 축제",
        titleEn = "Sapporo Summer Beer Festival",
        titleJa = "さっぽろ夏のビール祭り",
        city = "Sapporo", month = 8, day = 7,
        avgTempKo = "평균 22°C · 반팔",
        avgTempEn = "Avg 22°C · T-shirt",
        avgTempJa = "平均22°C・半袖",
        emoji = "🍺",
        tipKo = "홋카이도는 본토보다 시원해요. 저녁엔 가디건 챙기세요!",
        tipEn = "Hokkaido is cooler than mainland. Bring a cardigan for evenings!",
        tipJa = "北海道は本州より涼しい。夜はカーディガンを！",
        colorTag = "amber"
    ),
    SeasonEvent(
        titleKo = "교토·닛코 단풍 시즌",
        titleEn = "Kyoto & Nikko Autumn Leaves",
        titleJa = "京都・日光 紅葉シーズン",
        city = "Kyoto / Nikko", month = 11, day = 15,
        avgTempKo = "평균 13°C · 니트+재킷",
        avgTempEn = "Avg 13°C · Knit + Jacket",
        avgTempJa = "平均13°C・ニット＋ジャケット",
        emoji = "🍁",
        tipKo = "단풍 절정! 히트텍 챙기고 두꺼운 코트 준비하세요.",
        tipEn = "Peak foliage! Bring thermal wear and a thick coat.",
        tipJa = "紅葉のピーク！ヒートテックと厚手のコートを。",
        colorTag = "coral"
    ),
    SeasonEvent(
        titleKo = "삿포로 눈 축제",
        titleEn = "Sapporo Snow Festival",
        titleJa = "さっぽろ雪まつり",
        city = "Sapporo", month = 2, day = 4,
        avgTempKo = "평균 -5°C · 롱패딩+핫팩",
        avgTempEn = "Avg -5°C · Puffer + Hand warmers",
        avgTempJa = "平均-5°C・ロングダウン＋カイロ",
        emoji = "⛄",
        tipKo = "히트텍+핫팩 필수! 방한 장갑·목도리도 꼭 챙기세요.",
        tipEn = "Thermal wear & hand warmers essential! Don't forget gloves & scarf.",
        tipJa = "ヒートテック・カイロ必須！手袋・マフラーも忘れずに。",
        colorTag = "blue"
    ),
)