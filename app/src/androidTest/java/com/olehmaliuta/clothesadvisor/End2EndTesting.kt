package com.olehmaliuta.clothesadvisor

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.olehmaliuta.clothesadvisor.ui.navigation.Screen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class End2EndTesting {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testFullUserJourney() {
        composeTestRule.onNodeWithTag(
            "screen__${Screen.ClothesList.name}")
            .assertExists()
        composeTestRule.onNodeWithTag(
            "add_item_button")
            .performClick()
        composeTestRule.onNodeWithTag(
            "screen__${Screen.EditClothingItem.name}")
            .assertExists()
        composeTestRule
            .onNodeWithTag("main_content_container")
            .performScrollToNode(hasTestTag("cancel_button"))
        composeTestRule.onNodeWithTag(
            "cancel_button")
            .performClick()
        composeTestRule.onNodeWithTag(
            "screen__${Screen.ClothesList.name}")
            .assertExists()
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.OutfitList.name}")
            .performClick()
        composeTestRule.onNodeWithTag(
            "screen__${Screen.OutfitList.name}")
            .assertExists()
        composeTestRule.onNodeWithTag(
            "add_outfit_button")
            .performClick()
        composeTestRule.onNodeWithTag(
            "screen__${Screen.EditOutfit.name}")
            .assertExists()
        composeTestRule
            .onNodeWithTag("main_content_container")
            .performScrollToNode(hasTestTag("cancel_button"))
        composeTestRule.onNodeWithTag(
            "cancel_button")
            .performClick()
        composeTestRule.onNodeWithTag(
            "screen__${Screen.OutfitList.name}")
            .assertExists()
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.Generate.name}")
            .performClick()
        composeTestRule.onNodeWithTag(
            "screen__${Screen.Generate.name}")
            .assertExists()
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.Statistics.name}")
            .performClick()
        composeTestRule.onNodeWithTag(
            "screen__${Screen.Statistics.name}")
            .assertExists()
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.Settings.name}")
            .performClick()
        composeTestRule.onNodeWithTag(
            "screen__${Screen.Settings.name}")
            .assertExists()
        composeTestRule.onNodeWithTag(
            "log_in__button")
            .performClick()
        composeTestRule.onNodeWithTag(
            "screen__${Screen.LogIn.name}")
            .assertExists()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.onNodeWithTag(
            "screen__${Screen.Settings.name}")
            .assertExists()
        composeTestRule.onNodeWithTag(
            "sign_up__button")
            .performClick()
        composeTestRule.onNodeWithTag(
            "screen__${Screen.Registration.name}")
            .assertExists()
    }
}