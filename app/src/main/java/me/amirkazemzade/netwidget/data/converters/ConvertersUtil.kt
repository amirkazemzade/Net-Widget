package me.amirkazemzade.netwidget.data.converters

import me.amirkazemzade.netwidget.data.models.responses.CaptchaResponse
import me.amirkazemzade.netwidget.data.models.responses.PackagesResponse
import me.amirkazemzade.netwidget.data.models.responses.PackagesResponseItem
import me.amirkazemzade.netwidget.data.models.responses.RemainedResponse
import me.amirkazemzade.netwidget.domain.models.CaptchaBase64
import me.amirkazemzade.netwidget.domain.models.Cookie
import me.amirkazemzade.netwidget.domain.models.InternetPackage
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.RialPrice
import me.amirkazemzade.netwidget.domain.models.Traffic
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun CaptchaResponse.toCaptchaBase64() =
    CaptchaBase64(value = captcha)

fun Long.toTraffic() = Traffic(amountInMb = this)

fun String.toDurationUnit() = when (this) {
    "hour", "hours", "h" -> DurationUnit.HOURS
    "day", "days", "d" -> DurationUnit.DAYS
    "month", "months", "m" -> DurationUnit.DAYS
    "year", "years", "y" -> DurationUnit.DAYS
    else -> throw Exception("Unknown duration unit of `$this`")
}

fun Long.toRialPrice() = RialPrice(value = this)

fun PackagesResponseItem.toPackage() = InternetPackage(
    traffic = detail.internetTraffic.toTraffic(),
    duration = durationValue.toDuration(durationUnit.toDurationUnit()),
    price = price.toRialPrice(),
)

fun PackagesResponse.toPackagesArray(): Array<InternetPackage> =
    map { it.toPackage() }.toTypedArray()

fun String.toTraffic(unit: String) = Traffic(
    amountInMb = when (unit) {
        "ترابایت" -> toDouble() * 1024 * 1024
        "گیگابایت" -> toDouble() * 1024
        "مگابایت" -> toLong()
        else -> throw Exception("Unknown unit of `$unit`")
    }.toLong()
)

fun RemainedResponse.toRemained() = Remained(
    traffic = internetUsed.toTraffic(unit = internetUsedUnit),
    percentage = internetPercent.first().toFloat()/100,
)

fun String.toCookie(): Cookie {
    val map = trim().split(";")
        .mapNotNull { part ->
            val trimmed = part.trim()
            if (trimmed.isEmpty()) return@mapNotNull null
            val pieces = trimmed.split("=", limit = 2)
            val key = pieces[0].trim().lowercase()
            val value = if (pieces.size > 1) pieces[1].trim() else ""
            key to value
        }
        .toMap()

    return Cookie(
        session = map["session"] ?: "",
        path = map["path"] ?: "/",
        expires = map["expires"] ?: map["max-age"] ?: ""
    )
}

fun Cookie.toHeaderValue(): String = "session=$session"