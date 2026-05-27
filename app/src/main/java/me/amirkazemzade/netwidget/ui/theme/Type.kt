package me.amirkazemzade.netwidget.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.intl.Locale
import me.amirkazemzade.netwidget.R

val vazirmatnFontFamily = FontFamily(
    Font(R.font.vazirmatn_variable)
)

val Typography: Typography
    get() {
        val locale = Locale.current
        val defaultTypography = Typography()

        if (locale.language != "fa") {
            return defaultTypography
        }

        return Typography(
            displayLarge = defaultTypography.displayLarge.copy(fontFamily = vazirmatnFontFamily),
            displayMedium = defaultTypography.displayMedium.copy(fontFamily = vazirmatnFontFamily),
            displaySmall = defaultTypography.displaySmall.copy(fontFamily = vazirmatnFontFamily),
            headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = vazirmatnFontFamily),
            headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = vazirmatnFontFamily),
            headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = vazirmatnFontFamily),
            titleLarge = defaultTypography.titleLarge.copy(fontFamily = vazirmatnFontFamily),
            titleMedium = defaultTypography.titleMedium.copy(fontFamily = vazirmatnFontFamily),
            titleSmall = defaultTypography.titleSmall.copy(fontFamily = vazirmatnFontFamily),
            bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = vazirmatnFontFamily),
            bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = vazirmatnFontFamily),
            bodySmall = defaultTypography.bodySmall.copy(fontFamily = vazirmatnFontFamily),
            labelLarge = defaultTypography.labelLarge.copy(fontFamily = vazirmatnFontFamily),
            labelMedium = defaultTypography.labelMedium.copy(fontFamily = vazirmatnFontFamily),
            labelSmall = defaultTypography.labelSmall.copy(fontFamily = vazirmatnFontFamily),
        )
    }