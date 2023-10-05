package com.cedric.fgf

// Guided by Michael, not working nor doing anything.

data class Data(
    val after: String,
    val before: Any,
    val children: List<Children>,
    val dist: Int,
    val geo_filter: String,
    val modhash: String
)