package me.amirkazemzade.netwidget.domain.models

data class Traffic(
    val amountInMb: Long,
) {
    fun toMB(): Long = amountInMb
    fun toGB(): Float = amountInMb * 1f / 1024
}
