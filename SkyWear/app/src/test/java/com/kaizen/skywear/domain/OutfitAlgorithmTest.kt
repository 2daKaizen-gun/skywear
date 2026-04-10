package com.kaizen.skywear.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// 3-1 OutfitAlgorithmTest 단위 테스트

class OutfitAlgorithmTest {

    // 8단계 각각 올바른 stage 반환하는지 검증
    @Test
    fun `28도 이상이면 stage 1 반환`() {
        val result = getOutfitRecommendation(28.0)
        assertEquals(1, result.stage)
        assertEquals("반팔 + 반바지", result.mainOutfit)
    }

    @Test
    fun `23도에서 27도 사이면 stage 2 반환`() {
        val result = getOutfitRecommendation(25.0)
        assertEquals(2, result.stage)
    }

    @Test
    fun `17도에서 22도 사이면 stage 3 반환`() {
        val result = getOutfitRecommendation(20.0)
        assertEquals(3, result.stage)
    }

    @Test
    fun `12도에서 16도 사이면 stage 4 반환`() {
        val result = getOutfitRecommendation(14.0)
        assertEquals(4, result.stage)
    }

    @Test
    fun `9도에서 11도 사이면 stage 5 반환`() {
        val result = getOutfitRecommendation(10.0)
        assertEquals(5, result.stage)
    }

    @Test
    fun `5도에서 8도 사이면 stage 6 반환`() {
        val result = getOutfitRecommendation(6.0)
        assertEquals(6, result.stage)
    }

    @Test
    fun `0도에서 4도 사이면 stage 7 반환`() {
        val result = getOutfitRecommendation(2.0)
        assertEquals(7, result.stage)
    }

    @Test
    fun `영하이면 stage 8 반환`() {
        val result = getOutfitRecommendation(-5.0)
        assertEquals(8, result.stage)
        assertTrue(result.essentialItems.contains("핫팩"))
    }

    // 경계값 테스트
    @Test
    fun `정확히 28도면 stage 1 반환`() {
        assertEquals(1, getOutfitRecommendation(28.0).stage)
    }

    @Test
    fun `정확히 0도면 stage 7 반환`() {
        assertEquals(7, getOutfitRecommendation(0.0).stage)
    }

    @Test
    fun `정확히 영하 1도면 stage 8 반환`() {
        assertEquals(8, getOutfitRecommendation(-1.0).stage)
    }

    // 필수 아이템 포함 여부


    // 이모지 비어있는지 안 비어있는지


}