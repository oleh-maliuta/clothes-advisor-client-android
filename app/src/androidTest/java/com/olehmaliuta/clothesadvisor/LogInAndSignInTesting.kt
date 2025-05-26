package com.olehmaliuta.clothesadvisor

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.utils.LocaleConstants
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogInAndSignInTesting {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val userEmail = "test@gmail.com"
    private val userPassword = "pass"
    private val helper = UiTestHelper(composeTestRule)

    @Test
    fun failedSignUpDueToInvalidEmailFormat() {
        helper.assertExists("screen__${Screen.ClothesList.name}")
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.Settings.name}")
            .performClick()
        helper.assertExists("screen__${Screen.Settings.name}")
        composeTestRule.onNodeWithTag(
            "sign_up__button")
            .performClick()

        helper.register("111", "123")

        helper.assertExists(
            hasTestTag("info_dialog__description") and
                    hasText(LocaleConstants.getString(
                        "Invalid email format",
                        composeTestRule.activity))
        )
    }

    @Test
    fun failedSignUpDueToTheEmailAlreadyRegistered() {
        helper.assertExists("screen__${Screen.ClothesList.name}")
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.Settings.name}")
            .performClick()
        helper.assertExists("screen__${Screen.Settings.name}")
        composeTestRule.onNodeWithTag(
            "sign_up__button")
            .performClick()

        helper.register(userEmail, "123")

        helper.assertExists(
            hasTestTag("info_dialog__description") and
                    hasText(LocaleConstants.getString(
                        "Email is already taken",
                        composeTestRule.activity))
        )
    }

    @Test
    fun successfulLogIn() {
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
    }

    @Test
    fun failedLogIn() {
        helper.assertExists("screen__${Screen.ClothesList.name}")
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.Settings.name}")
            .performClick()
        helper.assertExists("screen__${Screen.Settings.name}")
        composeTestRule.onNodeWithTag(
            "log_in__button")
            .performClick()

        helper.authorize("111", "123")

        helper.assertExists(
            hasTestTag("info_dialog__description") and
                hasText(LocaleConstants.getString(
                    "User not found or incorrect password",
                    composeTestRule.activity))
        )
    }
}