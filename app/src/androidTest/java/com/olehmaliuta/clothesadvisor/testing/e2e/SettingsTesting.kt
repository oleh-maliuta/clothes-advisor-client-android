package com.olehmaliuta.clothesadvisor.testing.e2e

import android.content.Context
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.olehmaliuta.clothesadvisor.App
import com.olehmaliuta.clothesadvisor.MainActivity
import com.olehmaliuta.clothesadvisor.utils.navigation.Screen
import com.olehmaliuta.clothesadvisor.testing.UiTestHelper
import com.olehmaliuta.clothesadvisor.utils.localization.LanguageManager
import com.olehmaliuta.clothesadvisor.utils.LocaleConstants
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsTesting {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val userEmail = "test@gmail.com"
    private val userPassword = "pass"
    private val helper = UiTestHelper(composeTestRule)

    @Before
    fun setup() {
        helper.assertExists("screen__${Screen.ClothesList.name}")
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.Settings.name}")
            .performClick()
        helper.assertExists("screen__${Screen.Settings.name}")
        composeTestRule.onNodeWithTag(
            "log_in__button")
            .performClick()
        helper.authorize(userEmail, userPassword)

        helper.assertExists("screen__${Screen.ClothesList.name}")
        composeTestRule
            .onNodeWithTag("bottom_bar__navigation_button__${Screen.Settings.name}")
            .performClick()
        helper.assertExists("screen__${Screen.Settings.name}")
    }

    @After
    fun cleanup() {
        val prefs = composeTestRule.activity.getSharedPreferences(
            "user", Context.MODE_PRIVATE)
        prefs.edit {
            remove("token")
            remove("token_type")
        }
    }

    @Test
    fun successfulLanguageChanging() {
        val languageManager = (composeTestRule.activity.application as App).languageManager

        composeTestRule.onNodeWithTag("language_input")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag("language_input__item_system")
            .performClick()

        val systemLang = languageManager.getRealLanguage()

        assert(
            languageManager.getCurrentLanguage() == LanguageManager.SYSTEM_DEFAULT_LANGUAGE
        )
        assert(
            systemLang != LanguageManager.SYSTEM_DEFAULT_LANGUAGE
        )

        helper.assertExists(hasText(
            when (systemLang) {
                "en" -> "Settings"
                "uk" -> "Налаштування"
                else -> ""
            }
        ))

        composeTestRule.onNodeWithTag("language_input")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag("language_input__item_en")
            .performClick()

        assert(
            languageManager.getCurrentLanguage() == "en"
        )

        helper.assertExists(hasText("Settings"))

        composeTestRule.onNodeWithTag("language_input")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag("language_input__item_uk")
            .performClick()

        assert(
            languageManager.getCurrentLanguage() == "uk"
        )

        helper.assertExists(hasText("Налаштування"))

        composeTestRule.onNodeWithTag("language_input")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag("language_input__item_system")
            .performClick()
    }

    @Test
    fun failedEmailChangingDueToInvalidFormat() {
        composeTestRule
            .onNodeWithTag("change_email_form__email_input")
            .performScrollTo()
            .performTextInput("111")
        composeTestRule
            .onNodeWithTag("change_email_form__password_input")
            .performScrollTo()
            .performTextInput(userPassword)
        composeTestRule
            .onNodeWithTag("change_email_form__apply_button")
            .performScrollTo()
            .performClick()
        helper.assertExists(
            hasTestTag("info_dialog__description") and
                    hasText(LocaleConstants.getString(
                        "Invalid email format",
                        composeTestRule.activity))
        )
    }

    @Test
    fun successfulEmailChanging() {
        composeTestRule
            .onNodeWithTag("change_email_form__email_input")
            .performScrollTo()
            .performTextInput("1@gmail.com")
        composeTestRule
            .onNodeWithTag("change_email_form__password_input")
            .performScrollTo()
            .performTextInput(userPassword)
        composeTestRule
            .onNodeWithTag("change_email_form__apply_button")
            .performScrollTo()
            .performClick()
        helper.assertExists(
            hasTestTag("info_dialog__description") and
                    hasText(LocaleConstants.getString(
                        "Email successfully updated. Please verify new email",
                        composeTestRule.activity))
        )
    }

    @Test
    fun failedEmailChangingDueToInvalidPassword() {
        composeTestRule
            .onNodeWithTag("change_email_form__email_input")
            .performScrollTo()
            .performTextInput("1@gmail.com")
        composeTestRule
            .onNodeWithTag("change_email_form__password_input")
            .performScrollTo()
            .performTextInput("123")
        composeTestRule
            .onNodeWithTag("change_email_form__apply_button")
            .performScrollTo()
            .performClick()
        helper.assertExists(
            hasTestTag("info_dialog__description") and
                    hasText(LocaleConstants.getString(
                        "Incorrect password",
                        composeTestRule.activity))
        )
    }

    @Test
    fun successfulPasswordChanging() {
        composeTestRule
            .onNodeWithTag("change_password_form__old_password_input")
            .performScrollTo()
            .performTextInput(userPassword)
        composeTestRule
            .onNodeWithTag("change_password_form__new_password_input")
            .performScrollTo()
            .performTextInput("new-password")
        composeTestRule
            .onNodeWithTag("change_password_form__confirm_new_password_input")
            .performScrollTo()
            .performTextInput("new-password")
        composeTestRule
            .onNodeWithTag("change_password_form__apply_button")
            .performScrollTo()
            .performClick()
        helper.assertExists(
            hasTestTag("info_dialog__description") and
                    hasText(LocaleConstants.getString(
                        "Password successfully updated",
                        composeTestRule.activity))
        )
        composeTestRule
            .onNodeWithTag("info_dialog__ok_button")
            .performClick()
        composeTestRule
            .onNodeWithTag("log_out_button")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag(
            "log_in__button")
            .performClick()
        helper.authorize(userEmail, "new-password")
        helper.assertExists("screen__${Screen.ClothesList.name}")
        composeTestRule
            .onNodeWithTag("bottom_bar__navigation_button__${Screen.Settings.name}")
            .performClick()
        helper.assertExists("screen__${Screen.Settings.name}")
        composeTestRule
            .onNodeWithTag("change_password_form__old_password_input")
            .performScrollTo()
            .performTextInput("new-password")
        composeTestRule
            .onNodeWithTag("change_password_form__new_password_input")
            .performScrollTo()
            .performTextInput(userPassword)
        composeTestRule
            .onNodeWithTag("change_password_form__confirm_new_password_input")
            .performScrollTo()
            .performTextInput(userPassword)
        composeTestRule
            .onNodeWithTag("change_password_form__apply_button")
            .performScrollTo()
            .performClick()
        helper.assertExists(
            hasTestTag("info_dialog__description") and
                    hasText(LocaleConstants.getString(
                        "Password successfully updated",
                        composeTestRule.activity))
        )
    }

    @Test
    fun failedPasswordChangingDueToInvalidOldPassword() {
        composeTestRule
            .onNodeWithTag("change_password_form__old_password_input")
            .performScrollTo()
            .performTextInput("5412h")
        composeTestRule
            .onNodeWithTag("change_password_form__new_password_input")
            .performScrollTo()
            .performTextInput("new-password")
        composeTestRule
            .onNodeWithTag("change_password_form__confirm_new_password_input")
            .performScrollTo()
            .performTextInput("new-password")
        composeTestRule
            .onNodeWithTag("change_password_form__apply_button")
            .performScrollTo()
            .performClick()
        helper.assertExists(
            hasTestTag("info_dialog__description") and
                    hasText(LocaleConstants.getString(
                        "Incorrect old password",
                        composeTestRule.activity))
        )
    }
}