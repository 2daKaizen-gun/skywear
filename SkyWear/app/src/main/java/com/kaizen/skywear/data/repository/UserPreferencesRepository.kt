package com.kaizen.skywear.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow

// DataStore 기반 사용자 설정 관리
// 선택한 JP 도시, 온도 단위, 다크 모드 등 저장

// DataStore 싱글톤 선언 (Context 확장 프로퍼티)
private val Context.dataString: DataStore<Preferences> by preferencesDataStore(
    name = "skywear_preferences"
)

class UserPreferencesRepository(private val context: Context) {

    // DataStore 키 정의
    private object Keys {
        val SELECTED_KR_CITY = stringPreferencesKey("selected_kr_city")
        val SELECTED_JP_CITY = stringPreferencesKey("selected_jp_city")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val TEMP_UNIT = stringPreferencesKey("temp_unit") // celsius or fahrenheit
    }

    // 선택된 한국 도시 Flow


    // 선택된 일본 도시 Flow

    // 다크 모드 Flow

    // 온도 단위 Flow

    // 한국 도시 저장

    // 일본 도시 저장

    // 다크 모드 저장

    // 온도 단위 저장


}