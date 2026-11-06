package com.kagura.app.domain.usecase

import com.kagura.app.domain.model.Raridade
import org.junit.Assert.assertEquals
import org.junit.Test

class RarityCalculatorTest {

    @Test
    fun testLegendary() {
        val rarity = RarityCalculator.calculate(9.0f, 15000)
        assertEquals(Raridade.LENDARIO, rarity)
    }

    @Test
    fun testEpic() {
        val rarity = RarityCalculator.calculate(8.0f, 5000)
        assertEquals(Raridade.EPICO, rarity)
    }

    @Test
    fun testRare() {
        val rarity = RarityCalculator.calculate(7.0f, 2000)
        assertEquals(Raridade.RARO, rarity)
    }

    @Test
    fun testCommon() {
        val rarity = RarityCalculator.calculate(5.0f, 500)
        assertEquals(Raridade.COMUM, rarity)
    }
}
