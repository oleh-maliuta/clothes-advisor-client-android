package com.olehmaliuta.clothesadvisor.utils

import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.types.PaletteInfo
import com.olehmaliuta.clothesadvisor.navigation.NavItem
import com.olehmaliuta.clothesadvisor.navigation.Screen

object AppConstants {
    val navItems = listOf<NavItem>(
        NavItem(
            route = Screen.ClothesList,
            label = "Clothes",
            iconId = R.drawable.cloth
        ),
        NavItem(
            route = Screen.OutfitList,
            label = "Outfits",
            iconId = R.drawable.outfit
        ),
        NavItem(
            route = Screen.Generate,
            label = "Generate",
            iconId = R.drawable.generate
        ),
        NavItem(
            route = Screen.Statistics,
            label = "Statistics",
            iconId = R.drawable.statistics
        ),
        NavItem(
            route = Screen.Settings,
            label = "Settings",
            iconId = R.drawable.settings
        ),
    )
    val palettes = mapOf<String, PaletteInfo>(
        "monochromatic" to PaletteInfo(
            name = "Monochromatic",
            description = "It involves using one color, its tones and shades. " +
                    "Monochromatic color harmony is always a preferred choice. " +
                    "Due to the unique contrast of shades, it makes the design " +
                    "more attractive.",
            imageId = R.drawable.monochromatic_palette
        ),
        "analogous" to PaletteInfo(
            name = "Analogous",
            description = "Analogous palettes are based on colors that are " +
                    "adjacent to each other on the color wheel. Such palettes " +
                    "are used when it is necessary to create a design without " +
                    "contrasts. In website design, this means maintaining color " +
                    "harmony by filling the page background with analogous colors.",
            imageId = R.drawable.analogous_palette
        ),
        "complementary" to PaletteInfo(
            name = "Complementary",
            description = "A complementary palette is built on the basis of two " +
                    "opposite colors on the wheel and is used to create a " +
                    "contrasting effect.",
            imageId = R.drawable.complementary_palette
        ),
        "split_complementary" to PaletteInfo(
            name = "Split-complementary",
            description = "A split-complementary scheme is based on " +
                    "the same contrasting pair of colors, only in this case " +
                    "one of them is split into two adjacent ones.",
            imageId = R.drawable.split_complementary_palette
        ),
        "triadic" to PaletteInfo(
            name = "Triadic",
            description = "A triadic color scheme is constructed from three " +
                    "equidistant colors on the color wheel. This palette " +
                    "allows for both contrast and harmony.",
            imageId = R.drawable.triadic_palette
        ),
        "rectangle" to PaletteInfo(
            name = "Rectangular",
            description = "A rectangular palette includes two pairs of " +
                    "complementary colors. The most experienced designers " +
                    "use it to create fascinating images.",
            imageId = R.drawable.rectangular_palette
        ),
    )
}