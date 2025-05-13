package com.olehmaliuta.clothesadvisor.utils

import java.util.Locale

object LocaleConstants {
    private val strings = mapOf<String, Map<String, String>>(
        "Not authenticated" to mapOf(
            "uk" to "Не автентифіковано"
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
        "Invalid email or password" to mapOf(
            "uk" to "Неправильна адреса електронної пошти або пароль"
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
        return strings[value]?.get(language).toString()
    }

    fun getSecondLangCodeByFirst(
        value: String
    ): String? {
        for (el in langCodeKeyAlternatives) {
            if (el.first == value) {
                return el.second
            }
        }
        return null
    }
}