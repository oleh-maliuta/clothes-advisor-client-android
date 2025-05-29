package com.olehmaliuta.clothesadvisor.testing.e2e

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.olehmaliuta.clothesadvisor.MainActivity
import com.olehmaliuta.clothesadvisor.utils.navigation.Screen
import com.olehmaliuta.clothesadvisor.testing.UiTestHelper
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClothesManagementTesting {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            GrantPermissionRule.grant(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GrantPermissionRule.grant(
                Manifest.permission.READ_MEDIA_IMAGES,
            )
    } else {
            GrantPermissionRule.grant(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
    }

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
    fun successfulFavoriteValueToggling() {
        val container = composeTestRule.onNodeWithTag("main_content_container")

        container.performScrollToNode(
            hasTestTag("clothing_item_card") and
                    hasAnyChild(
                        hasTestTag("clothing_item_card__favorite_button") and
                                hasContentDescription("Regular")
                    )
        )

        val card = composeTestRule.onAllNodes(
            hasTestTag("clothing_item_card") and
                    hasAnyChild(
                        hasTestTag("clothing_item_card__favorite_button") and
                                hasContentDescription("Regular")
                    )
        ).onFirst()
        val descriptions = card.fetchSemanticsNode().config
            .getOrNull(SemanticsProperties.ContentDescription) ?: emptyList()
        val key = descriptions.firstOrNull {
            val keys = it.split('/')
            keys.size == 2 &&
                    keys.first() == "ClothingItemKey" &&
                    keys.last().toLongOrNull() != null
        }
        assert(key != null)
        val favoriteButton = composeTestRule.onNode(
            hasTestTag("clothing_item_card__favorite_button") and
                    hasParent(hasContentDescription(descriptions.first { it == key })),
            useUnmergedTree = true
        )
        assert(
            favoriteButton.fetchSemanticsNode().config
                .getOrNull(SemanticsProperties.ContentDescription)?.any { it == "Regular" } == true
        )

        favoriteButton.performClick()

        composeTestRule.waitUntil(UiTestHelper.DEFAULT_TIMEOUT) {
            favoriteButton.fetchSemanticsNode().config
                .getOrNull(SemanticsProperties.ContentDescription)?.any { it == "Favorite" } == true
        }

        favoriteButton.performClick()

        composeTestRule.waitUntil(UiTestHelper.DEFAULT_TIMEOUT) {
            favoriteButton.fetchSemanticsNode().config
                .getOrNull(SemanticsProperties.ContentDescription)?.any { it == "Regular" } == true
        }
    }

    @Test
    fun successfulClothingItemEditing() {
        var card = composeTestRule.onAllNodes(
            hasTestTag("clothing_item_card"),
            useUnmergedTree = true
        ).onFirst()
        var descriptions = card.fetchSemanticsNode().config
            .getOrNull(SemanticsProperties.ContentDescription) ?: emptyList()
        var key = descriptions.firstOrNull {
            val keys = it.split('/')
            keys.size == 2 &&
                    keys.first() == "ClothingItemKey" &&
                    keys.last().toLongOrNull() != null
        }
        assert(key != null)
        var favoriteButton = composeTestRule.onNode(
            hasTestTag("clothing_item_card__favorite_button") and
                    hasParent(hasContentDescription(descriptions.first { it == key })),
            useUnmergedTree = true
        )
        val isFavorite = favoriteButton.fetchSemanticsNode()
            .config[SemanticsProperties.ContentDescription].any { it == "Favorite" } == true

        card.performClick()
        helper.assertExists("screen__${Screen.EditClothingItem.name}")
        composeTestRule
            .onNodeWithTag("is_favorite_switch")
            .performScrollTo()
            .performClick()
        composeTestRule
            .onNodeWithTag("apply_button")
            .performScrollTo()
            .performClick()
        helper.assertExists(hasTestTag("apply_button") and isEnabled())
        helper.assertDoesNotExist(hasTestTag("info_dialog__description"))
        composeTestRule
            .onNodeWithTag("cancel_button")
            .performScrollTo()
            .performClick()
        helper.assertExists("screen__${Screen.ClothesList.name}")
        card = composeTestRule.onAllNodes(
            hasTestTag("clothing_item_card"),
            useUnmergedTree = true
        ).onFirst()
        descriptions = card.fetchSemanticsNode().config
            .getOrNull(SemanticsProperties.ContentDescription) ?: emptyList()
        key = descriptions.firstOrNull {
            val keys = it.split('/')
            keys.size == 2 &&
                    keys.first() == "ClothingItemKey" &&
                    keys.last().toLongOrNull() != null
        }
        assert(key != null)
        favoriteButton = composeTestRule.onNode(
            hasTestTag("clothing_item_card__favorite_button") and
                    hasParent(hasContentDescription(descriptions.first { it == key })),
            useUnmergedTree = true
        )
        var isFavoriteAgain = favoriteButton.fetchSemanticsNode()
            .config[SemanticsProperties.ContentDescription].any { it == "Favorite" } == true
        assert(isFavoriteAgain != isFavorite)

        card.performClick()
        helper.assertExists("screen__${Screen.EditClothingItem.name}")
        composeTestRule
            .onNodeWithTag("is_favorite_switch")
            .performScrollTo()
            .performClick()
        composeTestRule
            .onNodeWithTag("apply_button")
            .performScrollTo()
            .performClick()
        helper.assertExists(hasTestTag("apply_button") and isEnabled())
        helper.assertDoesNotExist(hasTestTag("info_dialog__description"))
        composeTestRule
            .onNodeWithTag("cancel_button")
            .performScrollTo()
            .performClick()
        helper.assertExists("screen__${Screen.ClothesList.name}")
        card = composeTestRule.onAllNodes(
            hasTestTag("clothing_item_card"),
            useUnmergedTree = true
        ).onFirst()
        descriptions = card.fetchSemanticsNode().config
            .getOrNull(SemanticsProperties.ContentDescription) ?: emptyList()
        key = descriptions.firstOrNull {
            val keys = it.split('/')
            keys.size == 2 &&
                    keys.first() == "ClothingItemKey" &&
                    keys.last().toLongOrNull() != null
        }
        assert(key != null)
        favoriteButton = composeTestRule.onNode(
            hasTestTag("clothing_item_card__favorite_button") and
                    hasParent(hasContentDescription(descriptions.first { it == key })),
            useUnmergedTree = true
        )
        isFavoriteAgain = favoriteButton.fetchSemanticsNode()
            .config[SemanticsProperties.ContentDescription].any { it == "Favorite" } == true
        assert(isFavoriteAgain == isFavorite)
    }

    @Test
    fun failedToAddElementDueToNecessaryValuesAbsence() {
        composeTestRule.onNodeWithTag(
            "add_item_button")
            .performClick()
        helper.assertExists("screen__${Screen.EditClothingItem.name}")
        composeTestRule
            .onNodeWithTag("apply_button")
            .performScrollTo()
            .assertIsNotEnabled()
    }
}