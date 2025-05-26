package com.olehmaliuta.clothesadvisor

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.olehmaliuta.clothesadvisor.navigation.Screen

class UiTestHelper(
    private val rule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
) {
    companion object {
        const val DEFAULT_TIMEOUT = 10000L
    }

    fun assertExists(
        tag: String
    ) {
        rule.waitUntil(DEFAULT_TIMEOUT) {
            rule.onAllNodesWithTag(tag)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    fun assertExists(
        matcher: SemanticsMatcher
    ) {
        rule.waitUntil(DEFAULT_TIMEOUT) {
            rule.onAllNodes(matcher)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    fun register(
        email: String,
        password: String,
        confirmPassword: String = password
    ) {
        assertExists("screen__${Screen.Registration.name}")
        rule.onNodeWithTag(
            "email__input")
            .performTextInput(email)
        rule.onNodeWithTag(
            "password__input")
            .performTextInput(password)
        rule.onNodeWithTag(
            "confirm_password__input")
            .performTextInput(confirmPassword)
        rule.onNodeWithTag(
            "apply__button")
            .performClick()
    }

    fun authorize(
        email: String,
        password: String
    ) {
        assertExists("screen__${Screen.LogIn.name}")
        rule.onNodeWithTag(
            "email__input")
            .performTextInput(email)
        rule.onNodeWithTag(
            "password__input")
            .performTextInput(password)
        rule.onNodeWithTag(
            "apply__button")
            .performClick()
    }
}