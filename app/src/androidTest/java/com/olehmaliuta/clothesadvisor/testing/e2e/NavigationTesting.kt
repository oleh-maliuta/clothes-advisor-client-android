package com.olehmaliuta.clothesadvisor.testing.e2e

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.olehmaliuta.clothesadvisor.MainActivity
import com.olehmaliuta.clothesadvisor.navigation.Screen
import com.olehmaliuta.clothesadvisor.testing.UiTestHelper
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTesting {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val helper = UiTestHelper(composeTestRule)

    @Test
    fun fullUserJourney() {
        helper.assertExists("screen__${Screen.ClothesList.name}")
        composeTestRule.onNodeWithTag(
            "add_item_button")
            .performClick()
        helper.assertExists("screen__${Screen.EditClothingItem.name}")
        composeTestRule
            .onNodeWithTag("main_content_container")
            .performScrollToNode(hasTestTag("cancel_button"))
        composeTestRule.onNodeWithTag(
            "cancel_button")
            .performClick()
        helper.assertExists("screen__${Screen.ClothesList.name}")
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.OutfitList.name}")
            .performClick()
        helper.assertExists("screen__${Screen.OutfitList.name}")
        composeTestRule.onNodeWithTag(
            "add_outfit_button")
            .performClick()
        helper.assertExists("screen__${Screen.EditOutfit.name}")
        composeTestRule
            .onNodeWithTag("main_content_container")
            .performScrollToNode(hasTestTag("cancel_button"))
        composeTestRule.onNodeWithTag(
            "cancel_button")
            .performClick()
        helper.assertExists("screen__${Screen.OutfitList.name}")
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.Generate.name}")
            .performClick()
        helper.assertExists("screen__${Screen.Generate.name}")
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.Statistics.name}")
            .performClick()
        helper.assertExists("screen__${Screen.Statistics.name}")
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.Settings.name}")
            .performClick()
        helper.assertExists("screen__${Screen.Settings.name}")
        composeTestRule.onNodeWithTag(
            "log_in__button")
            .performClick()
        helper.assertExists("screen__${Screen.LogIn.name}")
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        helper.assertExists("screen__${Screen.Settings.name}")
        composeTestRule.onNodeWithTag(
            "sign_up__button")
            .performClick()
        helper.assertExists("screen__${Screen.Registration.name}")
    }
}