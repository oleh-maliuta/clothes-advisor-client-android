package com.olehmaliuta.clothesadvisor

import com.olehmaliuta.clothesadvisor.utils.LocaleConstants
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

class LocaleConstantsTesting {
    private lateinit var originalLocale: Locale

    @Before
    fun setup() {
        originalLocale = Locale.getDefault()
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun `getString returns English when default locale is English`() {
        Locale.setDefault(Locale.ENGLISH)
        val input = "Not authenticated"
        val expected = input

        val result = LocaleConstants.getString(input)

        assertEquals(expected, result)
    }

    @Test
    fun `getString returns Ukrainian translation when locale is Ukrainian`() {
        Locale.setDefault(Locale("uk"))
        val input = "Not authenticated"
        val expected = "Не автентифіковано"

        val result = LocaleConstants.getString(input)

        assertEquals(expected, result)
    }

    @Test
    fun `getString returns input when translation doesn't exist`() {
        Locale.setDefault(Locale("uk"))
        val input = "Nonexistent string"
        val expected = input

        val result = LocaleConstants.getString(input)

        assertEquals(expected, result)
    }

    @Test
    fun `getString returns input when locale is not supported`() {
        Locale.setDefault(Locale.FRENCH)
        val input = "Database error"
        val expected = input

        val result = LocaleConstants.getString(input)

        assertEquals(expected, result)
    }

    @Test
    fun `getSecondLangCodeByFirst returns correct alternative for English`() {
        val input = "en"
        val expected = "en"

        val result = LocaleConstants.getSecondLangCodeByFirst(input)

        assertEquals(expected, result)
    }

    @Test
    fun `getSecondLangCodeByFirst returns correct alternative for Ukrainian`() {
        val input = "uk"
        val expected = "ua"

        val result = LocaleConstants.getSecondLangCodeByFirst(input)

        assertEquals(expected, result)
    }

    @Test
    fun `getSecondLangCodeByFirst returns default for unknown language`() {
        val input = "fr"
        val expected = "en"

        val result = LocaleConstants.getSecondLangCodeByFirst(input)

        assertEquals(expected, result)
    }

    @Test
    fun `verify all translations exist for Ukrainian locale`() {
        Locale.setDefault(Locale("uk"))

        LocaleConstants.getString("Not authenticated").also {
            assertEquals("Не автентифіковано", it)
        }

        LocaleConstants.getString("Internal server error").also {
            assertEquals("Внутрішня помилка сервера", it)
        }

        LocaleConstants.getString("Database error").also {
            assertEquals("Помилка бази даних", it)
        }
    }

    @Test
    fun `empty string input returns empty string`() {
        Locale.setDefault(Locale("uk"))
        val input = ""
        val expected = ""

        val result = LocaleConstants.getString(input)

        assertEquals(expected, result)
    }

    @Test
    fun `getString handles special characters`() {
        Locale.setDefault(Locale("uk"))
        val input = "Invalid email format"
        val expected = "Недійсний формат електронної пошти"

        val result = LocaleConstants.getString(input)

        assertEquals(expected, result)
    }

    @Test
    fun `getSecondLangCodeByFirst is case sensitive`() {
        val input = "UK"
        val expected = "en"

        val result = LocaleConstants.getSecondLangCodeByFirst(input)

        assertEquals(expected, result)
    }
}