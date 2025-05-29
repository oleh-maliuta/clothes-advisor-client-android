package com.olehmaliuta.clothesadvisor.testing.unit

import com.olehmaliuta.clothesadvisor.utils.roundToDecimals
import org.junit.Assert.assertEquals
import org.junit.Test

class FloatRoundToDecimalsTesting {
    @Test
    fun `round positive number to 2 decimals`() {
        val number = 3.14159f
        val result = number.roundToDecimals(2)
        assertEquals(3.14f, result)
    }

    @Test
    fun `round negative number to 3 decimals`() {
        val number = -2.71828f
        val result = number.roundToDecimals(3)
        assertEquals(-2.718f, result)
    }

    @Test
    fun `round to zero decimals`() {
        val number = 5.678f
        val result = number.roundToDecimals(0)
        assertEquals(6f, result)
    }

    @Test
    fun `round when decimal places equal exact half`() {
        val number = 2.345f
        val result = number.roundToDecimals(2)
        assertEquals(2.35f, result)
    }

    @Test
    fun `round already rounded number`() {
        val number = 7.25f
        val result = number.roundToDecimals(2)
        assertEquals(7.25f, result)
    }

    @Test
    fun `round with maximum decimal places`() {
        val number = 1.23456789f
        val result = number.roundToDecimals(7)
        assertEquals(1.2345679f, result)
    }

    @Test
    fun `round zero`() {
        val number = 0f
        val result = number.roundToDecimals(3)
        assertEquals(0f, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative decimal places should throw exception`() {
        val number = 1.23f
        number.roundToDecimals(-1)
    }
}