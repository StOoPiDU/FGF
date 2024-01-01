package com.cedric.fgf.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val DarkBlue = Color(0xFF006399)
val LighterDarkBlue = Color(0xFF4359A9)
sealed class ThemeColours(
    val background: Color,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val text: Color
){
    object Light: ThemeColours(
        background = Color.White,
        primary = DarkBlue,
        onPrimary = Color.White,
        secondary= LighterDarkBlue,
        text = Color.Black,
    )

    object Dark: ThemeColours(
        background = Color.Black,
        primary = Color(0xFF006399),
        onPrimary = Color.White,
        secondary = LighterDarkBlue,
        text = Color.White,
    )

    object Black: ThemeColours(
        background = Color.Black,
        primary = Color.Black,
        onPrimary = Color.White,
        secondary = Color.Red,
        text = Color.White,
    )
}