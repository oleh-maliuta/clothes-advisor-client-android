package com.olehmaliuta.clothesadvisor.testing.e2e

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.olehmaliuta.clothesadvisor.MainActivity
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.utils.navigation.Screen
import com.olehmaliuta.clothesadvisor.testing.UiTestHelper
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OutfitManagementTesting {
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
    fun successfulOutfitAddingEditingDeleting() {
        // Adding
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.OutfitList.name}")
            .performClick()
        helper.assertExists("screen__${Screen.OutfitList.name}")
        composeTestRule.onNodeWithTag(
            "add_outfit_button")
            .performClick()
        helper.assertExists("screen__${Screen.EditOutfit.name}")
        helper.assertExists("mode__add")
        composeTestRule
            .onNodeWithTag("clothing_item_card")
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithTag("name_input")
            .performTextInput("Test")
        composeTestRule
            .onNodeWithTag("edit_item_list_button")
            .performClick()
        helper.assertExists("select_items_for_outfit_dialog")
        composeTestRule
            .onAllNodesWithTag("clothing_item_mini_card")
            .onFirst()
            .performClick()
        composeTestRule
            .onNodeWithTag("select_items_for_outfit_dialog__done_button")
            .performClick()
        composeTestRule
            .onNodeWithTag("apply_button")
            .performClick()

        // Editing
        helper.assertExists("screen__${Screen.EditOutfit.name}")
        helper.assertExists("mode__edit")
        composeTestRule
            .onNodeWithTag("name_input")
            .performTextClearance()
        composeTestRule
            .onNodeWithTag("name_input")
            .performTextInput("NewTest")
        composeTestRule
            .onNodeWithTag("edit_item_list_button")
            .performClick()
        helper.assertExists("select_items_for_outfit_dialog")
        composeTestRule
            .onAllNodes(
                hasTestTag("clothing_item_mini_card__checkbox") and
                        hasContentDescription("Unchecked"),
                useUnmergedTree = true
            )
            .onFirst()
            .performClick()
        composeTestRule
            .onNodeWithTag("select_items_for_outfit_dialog__done_button")
            .performClick()
        composeTestRule
            .onAllNodesWithTag("clothing_item_card")
            .assertCountEquals(2)
        composeTestRule
            .onNodeWithTag("apply_button")
            .performClick()
        composeTestRule
            .onNodeWithTag("cancel_button")
            .performClick()
        helper.assertExists("screen__${Screen.OutfitList.name}")
        val outfitCards = composeTestRule.onAllNodes(
            hasTestTag("outfit_card") and
                    hasAnyChild(
                        hasTestTag("outfit_card__name") and
                                hasText("NewTest")
                    ),
            useUnmergedTree = true
        ).onFirst().assertExists()

        // Deleting
        outfitCards
            .performScrollTo()
            .performClick()
        helper.assertExists("screen__${Screen.EditOutfit.name}")
        helper.assertExists("mode__edit")
        composeTestRule
            .onNodeWithTag("delete_button")
            .performClick()
        helper.assertExists(
            hasTestTag("accept_cancel_dialog__title") and
            hasText(composeTestRule.activity.getString(R.string.edit_outfit__delete_outfit_message_title))
        )
        composeTestRule
            .onNodeWithTag("accept_cancel_dialog__confirm_button")
            .performClick()
        helper.assertExists("screen__${Screen.OutfitList.name}")
        helper.assertDoesNotExist(
            hasTestTag("outfit_card__item_count") and hasText("NewTest")
        )
    }

    @Test
    fun failedAddingOutfitDueToNameAbsence() {
        composeTestRule.onNodeWithTag(
            "bottom_bar__navigation_button__${Screen.OutfitList.name}")
            .performClick()
        helper.assertExists("screen__${Screen.OutfitList.name}")
        composeTestRule.onNodeWithTag(
            "add_outfit_button")
            .performClick()
        helper.assertExists("screen__${Screen.EditOutfit.name}")
        helper.assertExists("mode__add")
        composeTestRule
            .onNodeWithTag("clothing_item_card")
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithTag("name_input")
            .performTextClearance()
        composeTestRule
            .onNodeWithTag("edit_item_list_button")
            .performClick()
        helper.assertExists("select_items_for_outfit_dialog")
        composeTestRule
            .onAllNodesWithTag("clothing_item_mini_card")
            .onFirst()
            .performClick()
        composeTestRule
            .onNodeWithTag("select_items_for_outfit_dialog__done_button")
            .performClick()
        composeTestRule
            .onNodeWithTag("apply_button")
            .assertIsNotEnabled()
    }
}