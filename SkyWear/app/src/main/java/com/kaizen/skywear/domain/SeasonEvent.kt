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
    val colorTag: String,       // "green" / "blue" / "amber" / "coral"
    val destination: String     // "JP" or "KR"
) {
    fun dDay(): Long {
        val today = LocalDate.now()
        var target = LocalDate.of(today.year, month, day)
        if (target.isBefore(today)) target = target.plusYears(1)
        return ChronoUnit.DAYS.between(today, target)
    }
    fun isNow(): Boolean = dDay() <= 14
}

// 일본 여행 시즌 (KR→JP 방향)
val JP_SEASON_EVENTS = listOf(
    SeasonEvent(
        titleKo = "교토·도쿄 벚꽃 시즌", titleEn = "Kyoto & Tokyo Cherry Blossom", titleJa = "京都・東京 桜シーズン",
        city = "Kyoto / Tokyo", month = 3, day = 25,
        avgTempKo = "평균 10°C · 가벼운 코트", avgTempEn = "Avg 10°C · Light coat", avgTempJa = "平均10°C・軽いコート",
        emoji = "🌸",
        tipKo = "서울보다 약간 따뜻해요. 가벼운 코트 하나면 충분!",
        tipEn = "Slightly warmer than Seoul. A light coat is enough!",
        tipJa = "ソウルより少し暖かい。軽いコートで十分！",
        colorTag = "coral", destination = "JP"
    ),
    SeasonEvent(
        titleKo = "교토 신록 시즌", titleEn = "Kyoto Fresh Greenery", titleJa = "京都 新緑シーズン",
        city = "Kyoto", month = 5, day = 10,
        avgTempKo = "평균 20°C · 가디건", avgTempEn = "Avg 20°C · Cardigan", avgTempJa = "平均20°C・カーディガン",
        emoji = "🌿",
        tipKo = "서울과 기온 비슷. 관광객 적어 여행하기 딱 좋아요!",
        tipEn = "Similar to Seoul temps. Great time with fewer tourists!",
        tipJa = "ソウルと気温が近く、観光客も少なくて最適！",
        colorTag = "green", destination = "JP"
    ),
    SeasonEvent(
        titleKo = "오사카·도쿄 장마 시작", titleEn = "Osaka & Tokyo Rainy Season", titleJa = "大阪・東京 梅雨入り",
        city = "Osaka / Tokyo", month = 6, day = 8,
        avgTempKo = "평균 22°C · 우산 필수", avgTempEn = "Avg 22°C · Umbrella essential", avgTempJa = "平均22°C・傘必須",
        emoji = "☔",
        tipKo = "우산·우비 필수! 실내 관광 위주로 일정 짜세요.",
        tipEn = "Bring umbrella & raincoat! Plan indoor activities.",
        tipJa = "傘・レインコート必須！屋内観光中心のプランを。",
        colorTag = "blue", destination = "JP"
    ),
    SeasonEvent(
        titleKo = "오사카·교토 여름 축제 시즌", titleEn = "Osaka & Kyoto Summer Festival", titleJa = "大阪・京都 夏祭りシーズン",
        city = "Osaka / Kyoto", month = 7, day = 17,
        avgTempKo = "평균 30°C · 반팔+선크림", avgTempEn = "Avg 30°C · T-shirt + Sunscreen", avgTempJa = "平均30°C・半袖＋日焼け止め",
        emoji = "🎆",
        tipKo = "기온 높고 습해요. 시원한 옷 + 부채나 미니 선풍기 챙기세요.",
        tipEn = "Hot and humid. Wear light clothes and bring a fan.",
        tipJa = "暑くて湿気が多い。薄着と扇子・ミニ扇風機を持参して。",
        colorTag = "amber", destination = "JP"
    ),
    SeasonEvent(
        titleKo = "삿포로 여름 맥주 축제", titleEn = "Sapporo Summer Beer Festival", titleJa = "さっぽろ夏のビール祭り",
        city = "Sapporo", month = 8, day = 7,
        avgTempKo = "평균 22°C · 반팔", avgTempEn = "Avg 22°C · T-shirt", avgTempJa = "平均22°C・半袖",
        emoji = "🍺",
        tipKo = "홋카이도는 본토보다 시원해요. 저녁엔 가디건 챙기세요!",
        tipEn = "Hokkaido is cooler than mainland. Bring a cardigan for evenings!",
        tipJa = "北海道は本州より涼しい。夜はカーディガンを！",
        colorTag = "amber", destination = "JP"
    ),
    SeasonEvent(
        titleKo = "교토·닛코 단풍 시즌", titleEn = "Kyoto & Nikko Autumn Leaves", titleJa = "京都・日光 紅葉シーズン",
        city = "Kyoto / Nikko", month = 11, day = 15,
        avgTempKo = "평균 13°C · 니트+재킷", avgTempEn = "Avg 13°C · Knit + Jacket", avgTempJa = "平均13°C・ニット＋ジャケット",
        emoji = "🍁",
        tipKo = "단풍 절정! 히트텍 챙기고 두꺼운 코트 준비하세요.",
        tipEn = "Peak foliage! Bring thermal wear and a thick coat.",
        tipJa = "紅葉のピーク！ヒートテックと厚手のコートを。",
        colorTag = "coral", destination = "JP"
    ),
    SeasonEvent(
        titleKo = "도쿄·오사카 일루미네이션", titleEn = "Tokyo & Osaka Illumination", titleJa = "東京・大阪 イルミネーション",
        city = "Tokyo / Osaka", month = 12, day = 10,
        avgTempKo = "평균 8°C · 두꺼운 코트", avgTempEn = "Avg 8°C · Heavy coat", avgTempJa = "平均8°C・厚手コート",
        emoji = "🎄",
        tipKo = "야경이 아름다워요. 방한 필수! 핫초코 한 잔 곁들이면 완벽.",
        tipEn = "Beautiful night lights. Stay warm! Hot chocolate pairs perfectly.",
        tipJa = "夜景が美しい。防寒必須！ホットチョコレートと合わせて最高。",
        colorTag = "blue", destination = "JP"
    ),
    SeasonEvent(
        titleKo = "삿포로 눈 축제", titleEn = "Sapporo Snow Festival", titleJa = "さっぽろ雪まつり",
        city = "Sapporo", month = 2, day = 4,
        avgTempKo = "평균 -5°C · 롱패딩+핫팩", avgTempEn = "Avg -5°C · Puffer + Hand warmers", avgTempJa = "平均-5°C・ロングダウン＋カイロ",
        emoji = "⛄",
        tipKo = "히트텍+핫팩 필수! 방한 장갑·목도리도 꼭 챙기세요.",
        tipEn = "Thermal wear & hand warmers essential! Don't forget gloves & scarf.",
        tipJa = "ヒートテック・カイロ必須！手袋・マフラーも忘れずに。",
        colorTag = "blue", destination = "JP"
    ),
)

// 한국 여행 시즌 (JP→KR 방향)
val KR_SEASON_EVENTS = listOf(
    SeasonEvent(
        titleKo = "서울·제주 벚꽃 시즌", titleEn = "Seoul & Jeju Cherry Blossom", titleJa = "ソウル・済州島 桜シーズン",
        city = "Seoul / Jeju", month = 4, day = 5,
        avgTempKo = "평균 12°C · 가벼운 코트", avgTempEn = "Avg 12°C · Light coat", avgTempJa = "平均12°C・軽いコート",
        emoji = "🌸",
        tipKo = "여의도 한강 벚꽃 축제 추천! 주말엔 사람 많으니 평일 추천.",
        tipEn = "Yeouido Hangang cherry blossom festival! Weekdays are less crowded.",
        tipJa = "汝矣島漢江の桜祭りがおすすめ！平日の方が空いています。",
        colorTag = "coral", destination = "KR"
    ),
    SeasonEvent(
        titleKo = "한강 피크닉 시즌", titleEn = "Hangang River Picnic Season", titleJa = "漢江ピクニックシーズン",
        city = "Seoul", month = 5, day = 15,
        avgTempKo = "평균 18°C · 가디건", avgTempEn = "Avg 18°C · Cardigan", avgTempJa = "平均18°C・カーディガン",
        emoji = "🧺",
        tipKo = "한강 편의점 치킨+맥주 필수 코스! 야경도 아름다워요.",
        tipEn = "Convenience store chicken & beer by the Han River is a must!",
        tipJa = "漢江のコンビニチキン＋ビールは必須コース！夜景も綺麗。",
        colorTag = "green", destination = "KR"
    ),
    SeasonEvent(
        titleKo = "부산 바다 시즌", titleEn = "Busan Beach Season", titleJa = "釜山 海水浴シーズン",
        city = "Busan", month = 7, day = 20,
        avgTempKo = "평균 27°C · 반팔+수영복", avgTempEn = "Avg 27°C · T-shirt + Swimwear", avgTempJa = "平均27°C・半袖＋水着",
        emoji = "🏖️",
        tipKo = "해운대·광안리 최고의 시즌! 자외선 차단제 필수.",
        tipEn = "Best season for Haeundae & Gwangalli beaches! Sunscreen essential.",
        tipJa = "海雲台・広安里ビーチの最高シーズン！日焼け止め必須。",
        colorTag = "amber", destination = "KR"
    ),
    SeasonEvent(
        titleKo = "서울 단풍 시즌", titleEn = "Seoul Autumn Foliage", titleJa = "ソウル 紅葉シーズン",
        city = "Seoul", month = 10, day = 25,
        avgTempKo = "평균 14°C · 니트+재킷", avgTempEn = "Avg 14°C · Knit + Jacket", avgTempJa = "平均14°C・ニット＋ジャケット",
        emoji = "🍁",
        tipKo = "남산·북한산 단풍이 아름다워요. 일교차 크니 겉옷 필수!",
        tipEn = "Namsan & Bukhansan autumn leaves are stunning. Bring a jacket!",
        tipJa = "南山・北漢山の紅葉が美しい。朝晩の気温差が大きいので上着必須！",
        colorTag = "coral", destination = "KR"
    ),
    SeasonEvent(
        titleKo = "전주 한옥마을 단풍", titleEn = "Jeonju Hanok Village Autumn", titleJa = "全州 韓屋村 紅葉",
        city = "Jeonju", month = 11, day = 5,
        avgTempKo = "평균 10°C · 가벼운 코트", avgTempEn = "Avg 10°C · Light coat", avgTempJa = "平均10°C・軽いコート",
        emoji = "🏯",
        tipKo = "전통 한복 체험 + 비빔밥 필수! 가을 정취가 아름다워요.",
        tipEn = "Hanbok experience + Bibimbap is a must! Beautiful autumn atmosphere.",
        tipJa = "韓服体験＋ビビンバは必須！秋の風情が美しいです。",
        colorTag = "amber", destination = "KR"
    ),
    SeasonEvent(
        titleKo = "서울 크리스마스 마켓", titleEn = "Seoul Christmas Market", titleJa = "ソウル クリスマスマーケット",
        city = "Seoul", month = 12, day = 15,
        avgTempKo = "평균 0°C · 롱패딩", avgTempEn = "Avg 0°C · Long puffer", avgTempJa = "平均0°C・ロングダウン",
        emoji = "🎅",
        tipKo = "명동·코엑스 크리스마스 분위기 최고! 방한 철저히 하세요.",
        tipEn = "Myeongdong & COEX Christmas atmosphere is magical! Dress warmly.",
        tipJa = "明洞・COEXのクリスマスムードは最高！しっかり防寒を。",
        colorTag = "blue", destination = "KR"
    ),
    SeasonEvent(
        titleKo = "제주 동백꽃 시즌", titleEn = "Jeju Camellia Season", titleJa = "済州島 椿シーズン",
        city = "Jeju", month = 1, day = 20,
        avgTempKo = "평균 6°C · 두꺼운 코트", avgTempEn = "Avg 6°C · Heavy coat", avgTempJa = "平均6°C・厚手コート",
        emoji = "🌺",
        tipKo = "겨울에도 꽃이 피는 제주! 바람이 강하니 방풍 재킷 필수.",
        tipEn = "Flowers bloom even in winter in Jeju! Windproof jacket essential.",
        tipJa = "冬でも花が咲く済州島！風が強いので防風ジャケット必須。",
        colorTag = "coral", destination = "KR"
    ),
    SeasonEvent(
        titleKo = "강원도 스키 시즌", titleEn = "Gangwon Ski Season", titleJa = "江原道 スキーシーズン",
        city = "Gangwon", month = 1, day = 10,
        avgTempKo = "평균 -8°C · 방한+스키복", avgTempEn = "Avg -8°C · Ski gear + Thermal", avgTempJa = "平均-8°C・スキーウェア＋防寒",
        emoji = "⛷️",
        tipKo = "평창·하이원 리조트 추천! 히트텍+장갑+고글 필수.",
        tipEn = "Pyeongchang & High1 Resort recommended! Thermal wear + gloves + goggles.",
        tipJa = "平昌・ハイワンリゾートがおすすめ！ヒートテック＋手袋＋ゴーグル必須。",
        colorTag = "blue", destination = "KR"
    ),
)