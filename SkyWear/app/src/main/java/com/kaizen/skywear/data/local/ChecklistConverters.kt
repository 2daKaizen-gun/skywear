package com.kaizen.skywear.data.local

import androidx.room.TypeConverter

// room DB는 enum을 직접 저장하지 못함
// ChecklistCategory <-> String 변환 담당

class ChecklistConverters {

    // ChecklistCategory -> String (DB 저장 시)
    @TypeConverter
    fun fromCategory(category: ChecklistCategory): String = category.name

    // String -> ChecklistCategory (DB 읽기 시)
    @TypeConverter
    fun toCategory(value: String): ChecklistCategory =
        ChecklistCategory.valueOf(value)
}