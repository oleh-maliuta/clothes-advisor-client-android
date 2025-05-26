package com.olehmaliuta.clothesadvisor.utils

import android.app.Activity
import android.content.Context
import com.olehmaliuta.clothesadvisor.App
import java.util.Locale

object LocaleConstants {
    private const val DEFAULT_LANG = "en"

    private val strings = mapOf<String, Map<String, String>>(
        "Not authenticated" to mapOf(
            "uk" to "Не автентифіковано"
        ),
        "Internal server error" to mapOf(
            "uk" to "Внутрішня помилка сервера"
        ),
        "Database error" to mapOf(
            "uk" to "Помилка бази даних"
        ),
        "Register successful. Please verify your email" to mapOf(
            "uk" to "Реєстрація успішна. Підтвердьте свою електронну адресу"
        ),
        "Invalid email format" to mapOf(
            "uk" to "Недійсний формат електронної пошти"
        ),
        "Email is already taken" to mapOf(
            "uk" to "Електронна пошта вже зайнята"
        ),
        "User not found or incorrect password" to mapOf(
            "uk" to "Користувача не знайдено або неправильний пароль"
        ),
        "Email not verified" to mapOf(
            "uk" to "Електронна адреса не підтверджена"
        ),
        "Password reset email sent successfully." to mapOf(
            "uk" to "Електронний лист для зміни пароля успішно надіслано."
        ),
        "User not found" to mapOf(
            "uk" to "Користувача не знайдено"
        ),
        "Incorrect password" to mapOf(
            "uk" to "Невірний пароль"
        ),
        "Email successfully updated. Please verify new email" to mapOf(
            "uk" to "Електронну адресу успішно оновлено. Підтвердьте нову електронну адресу"
        ),
        "Incorrect old password" to mapOf(
            "uk" to "Неправильний старий пароль"
        ),
        "Password successfully updated" to mapOf(
            "uk" to "Пароль успішно оновлено"
        ),
        "Item limit reached. Maximum 100 clothing items allowed per user." to mapOf(
            "uk" to "Обмеження кількості елементів. Максимально 100 елементів одягу дозволено на одного користувача."
        ),
        "Invalid color values" to mapOf(
            "uk" to "Недійсні значення кольору"
        ),
        "Clothing item added successfully." to mapOf(
            "uk" to "Елемент одягу було успішно додано."
        ),
        "Clothing item updated successfully." to mapOf(
            "uk" to "Елемент одягу було успішно оновлено."
        ),
        "Clothing combination created successfully." to mapOf(
            "uk" to "Набір було успішно додано."
        ),
        "Clothing combination updated successfully." to mapOf(
            "uk" to "Набір було успішно оновлено."
        ),
        "Clothing item not found" to mapOf(
            "uk" to "Елемент одягу не знайдено"
        ),
        "Combination not found" to mapOf(
            "uk" to "Набір не знайдено"
        ),
        "Some clothing items from request not found." to mapOf(
            "uk" to "Деякі елементи одягу із запиту не знайдено."
        ),
    )

    private val langCodeKeyAlternatives = listOf<Pair<String, String>>(
        Pair("en", "en"),
        Pair("uk", "ua"),
    )

    fun getString(
        value: String,
        context: Context
    ): String {
        val application = context.applicationContext as App
        val language = application.languageManager.getCurrentLanguage()

        if (language == DEFAULT_LANG) {
            return value
        }

        return strings[value]?.get(language) ?: value
    }

    fun getSecondLangCodeByFirst(
        value: String
    ): String {
        for (el in langCodeKeyAlternatives) {
            if (el.first == value) {
                return el.second
            }
        }
        return DEFAULT_LANG
    }
}