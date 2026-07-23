package me.amirkazemzade.netwidget.domain.models

data class Remained(
    val traffic: Traffic,
    /** The remain percentage as a float number between 0 and 1. **/
    val percentage: Float,
)
