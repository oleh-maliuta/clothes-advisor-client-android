package com.olehmaliuta.clothesadvisor

import android.Manifest
import android.content.Context
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.olehmaliuta.clothesadvisor.navigation.Screen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeneratingTesting {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

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
    fun successGenerating() {
        composeTestRule
            .onNodeWithTag("bottom_bar__navigation_button__${Screen.Generate.name}")
            .performClick()
        helper.assertExists("screen__${Screen.Generate.name}")
        composeTestRule
            .onNodeWithTag("main_content_container")
            .performScrollToNode(hasTestTag("generate_button"))
        composeTestRule
            .onNodeWithTag("generate_button")
            .performClick()
        helper.assertExists("weather_info_card")
        helper.assertExists("outfit_card")
        composeTestRule
            .onAllNodesWithTag("outfit_card__save_button")
            .onFirst()
            .performScrollTo()
            .performClick()
        helper.assertExists("screen__${Screen.EditOutfit.name}")
    }

    @Test
    fun failedGeneratingDueToNotSelectedLocation() {
        composeTestRule
            .onNodeWithTag("bottom_bar__navigation_button__${Screen.Generate.name}")
            .performClick()
        helper.assertExists("screen__${Screen.Generate.name}")
        composeTestRule
            .onNodeWithTag("use_device_location_switch")
            .performClick()
        composeTestRule
            .onNodeWithTag("main_content_container")
            .performScrollToNode(hasTestTag("generate_button"))
        composeTestRule
            .onNodeWithTag("generate_button")
            .assertIsNotEnabled()
    }
}