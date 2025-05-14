package com.olehmaliuta.clothesadvisor.utils

import java.util.Locale

object LocaleConstants {
    private const val DEFAULT_LANG = "en"

    private val strings = mapOf<String, Map<String, String>>(
        // http (authorization)
        "Not authenticated" to mapOf(
            "uk" to "Не автентифіковано"
        ),

        // http - /register
        "Register successful. Please verify your email" to mapOf(
            "uk" to "Реєстрація успішна. Підтвердьте свою електронну адресу"
        ),
        "Invalid email format" to mapOf(
            "uk" to "Недійсний формат електронної пошти"
        ),
        "Email is already taken" to mapOf(
            "uk" to "Електронна пошта вже зайнята"
        ),

        // http - /login_with_email
        "User not found or incorrect password" to mapOf(
            "uk" to "Користувача не знайдено або неправильний пароль"
        ),
        "Email not verified" to mapOf(
            "uk" to "Електронна адреса не підтверджена"
        ),

        // http - /forgot-password
        "Password reset email sent successfully." to mapOf(
            "uk" to "Електронний лист для зміни пароля успішно надіслано."
        ),
        "User not found" to mapOf(
            "uk" to "Користувача не знайдено"
        ),
    )

    private val langCodeKeyAlternatives = listOf<Pair<String, String>>(
        Pair("en", "en"),
        Pair("uk", "ua"),
    )

    fun getString(
        value: String
    ): String {
        val language = Locale.getDefault().language

        if (language == DEFAULT_LANG) {
            return value
        }

        return strings[value]?.get(language).toString()
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