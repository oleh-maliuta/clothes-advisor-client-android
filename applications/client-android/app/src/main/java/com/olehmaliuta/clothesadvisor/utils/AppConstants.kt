package com.olehmaliuta.clothesadvisor.utils

import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.utils.types.PaletteInfo
import com.olehmaliuta.clothesadvisor.utils.navigation.NavItem
import com.olehmaliuta.clothesadvisor.utils.navigation.Screen
import com.olehmaliuta.clothesadvisor.utils.localization.LanguageManager

object AppConstants {
    const val MAX_CLOTHING_ITEMS = 100
    const val MAX_OUTFITS = 50

    val navItems = listOf(
        NavItem(
            route = Screen.ClothesList,
            labelId = R.string.bottom_bar__clothes__label,
            iconId = R.drawable.cloth
        ),
        NavItem(
            route = Screen.OutfitList,
            labelId = R.string.bottom_bar__outfits__label,
            iconId = R.drawable.outfit
        ),
        NavItem(
            route = Screen.Generate,
            labelId = R.string.bottom_bar__generate__label,
            iconId = R.drawable.generate
        ),
        NavItem(
            route = Screen.Statistics,
            labelId = R.string.bottom_bar__statistics__label,
            iconId = R.drawable.statistics
        ),
        NavItem(
            route = Screen.Settings,
            labelId = R.string.bottom_bar__settings__label,
            iconId = R.drawable.settings
        ),
    )
    val seasons = mapOf(
        "spring" to R.string.seasons__spring,
        "summer" to R.string.seasons__summer,
        "autumn" to R.string.seasons__autumn,
        "winter" to R.string.seasons__winter,
    )
    val categories = mapOf(
        "tshirt" to R.string.categories__t_shirt,
        "pants" to R.string.categories__pants,
        "jacket" to R.string.categories__jacket,
        "dress" to R.string.categories__dress,
        "skirt" to R.string.categories__skirt,
        "shorts" to R.string.categories__shorts,
        "hoodie" to R.string.categories__hoodie,
        "sweater" to R.string.categories__sweater,
        "coat" to R.string.categories__coat,
        "blouse" to R.string.categories__blouse,
        "shoes" to R.string.categories__shoes,
        "accessories" to R.string.categories__accessories,
        "boots" to R.string.categories__boots,
        "sneakers" to R.string.categories__sneakers,
        "sandals" to R.string.categories__sandals,
        "hat" to R.string.categories__hat,
        "scarf" to R.string.categories__scarf,
        "gloves" to R.string.categories__gloves,
        "socks" to R.string.categories__socks,
        "underwear" to R.string.categories__underwear,
        "swimwear" to R.string.categories__swimwear,
        "belt" to R.string.categories__belt,
        "bag" to R.string.categories__bag,
        "watch" to R.string.categories__watch,
        "jeans" to R.string.categories__jeans,
        "leggings" to R.string.categories__leggings,
        "tank_top" to R.string.categories__tank_top,
        "overalls" to R.string.categories__overalls,
        "beanie" to R.string.categories__beanie,
    )
    val events = mapOf(
        "casual_walk" to R.string.events__casual_walk,
        "picnic" to R.string.events__picnic,
        "date" to R.string.events__date,
        "work_meeting" to R.string.events__work_meeting,
        "hiking" to R.string.events__hiking,
        "beach_party" to R.string.events__beach_party,
        "gym" to R.string.events__gym,
        "formal_event" to R.string.events__formal_event,
        "home_relax" to R.string.events__home_relax,
        "sport_training" to R.string.events__sport_training,
    )
    val palettes = mapOf(
        "monochromatic" to PaletteInfo(
            nameId = R.string.palettes__monochromatic__name,
            descriptionId = R.string.palettes__monochromatic__description,
            imageId = R.drawable.monochromatic_palette
        ),
        "analogous" to PaletteInfo(
            nameId = R.string.palettes__analogous__name,
            descriptionId = R.string.palettes__analogous__description,
            imageId = R.drawable.analogous_palette
        ),
        "complementary" to PaletteInfo(
            nameId = R.string.palettes__complementary__name,
            descriptionId = R.string.palettes__complementary__description,
            imageId = R.drawable.complementary_palette
        ),
        "split_complementary" to PaletteInfo(
            nameId = R.string.palettes__split_complementary__name,
            descriptionId = R.string.palettes__split_complementary__description,
            imageId = R.drawable.split_complementary_palette
        ),
        "triadic" to PaletteInfo(
            nameId = R.string.palettes__triadic__name,
            descriptionId = R.string.palettes__triadic__description,
            imageId = R.drawable.triadic_palette
        ),
        "rectangle" to PaletteInfo(
            nameId = R.string.palettes__rectangle__name,
            descriptionId = R.string.palettes__rectangle__description,
            imageId = R.drawable.rectangular_palette
        ),
    )
    val languages: Map<String, Int> = mapOf(
        LanguageManager.SYSTEM_DEFAULT_LANGUAGE to R.string.settings__personalization__language__system,
        "en" to R.string.settings__personalization__language__en,
        "uk" to R.string.settings__personalization__language__uk,
    )
    val weatherTypes: Map<Int, Int> = mapOf(
        200 to R.string.weather_types__200,
        201 to R.string.weather_types__201,
        202 to R.string.weather_types__202,
        210 to R.string.weather_types__210,
        211 to R.string.weather_types__211,
        212 to R.string.weather_types__212,
        221 to R.string.weather_types__221,
        230 to R.string.weather_types__230,
        231 to R.string.weather_types__231,
        232 to R.string.weather_types__232,
        300 to R.string.weather_types__300,
        301 to R.string.weather_types__301,
        302 to R.string.weather_types__302,
        310 to R.string.weather_types__310,
        311 to R.string.weather_types__311,
        312 to R.string.weather_types__312,
        313 to R.string.weather_types__313,
        314 to R.string.weather_types__314,
        321 to R.string.weather_types__321,
        500 to R.string.weather_types__500,
        501 to R.string.weather_types__501,
        502 to R.string.weather_types__502,
        503 to R.string.weather_types__503,
        504 to R.string.weather_types__504,
        511 to R.string.weather_types__511,
        520 to R.string.weather_types__520,
        521 to R.string.weather_types__521,
        522 to R.string.weather_types__522,
        531 to R.string.weather_types__531,
        600 to R.string.weather_types__600,
        601 to R.string.weather_types__601,
        602 to R.string.weather_types__602,
        611 to R.string.weather_types__611,
        612 to R.string.weather_types__612,
        613 to R.string.weather_types__613,
        615 to R.string.weather_types__615,
        616 to R.string.weather_types__616,
        620 to R.string.weather_types__620,
        621 to R.string.weather_types__621,
        622 to R.string.weather_types__622,
        701 to R.string.weather_types__701,
        711 to R.string.weather_types__711,
        721 to R.string.weather_types__721,
        731 to R.string.weather_types__731,
        741 to R.string.weather_types__741,
        751 to R.string.weather_types__751,
        761 to R.string.weather_types__761,
        762 to R.string.weather_types__762,
        771 to R.string.weather_types__771,
        781 to R.string.weather_types__781,
        800 to R.string.weather_types__800,
        801 to R.string.weather_types__801,
        802 to R.string.weather_types__802,
        803 to R.string.weather_types__803,
        804 to R.string.weather_types__804
    )
}