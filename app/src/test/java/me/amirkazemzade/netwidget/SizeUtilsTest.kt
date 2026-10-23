package me.amirkazemzade.netwidget

import me.amirkazemzade.netwidget.ui.utils.calculateOptimalAmountFontSizeSp
import me.amirkazemzade.netwidget.ui.utils.calculateOptimalPercentageFontSizeSp
import me.amirkazemzade.netwidget.ui.utils.calculateOptimalSuffixFontSizeSp
import me.amirkazemzade.netwidget.ui.utils.calculateOptimalTrafficFontSizeSp
import org.junit.Assert.assertTrue
import org.junit.Test

class SizeUtilsTest {

    @Test
    fun percentageFontSize_decreasesWithSmallerDiameter() {
        val largeSize = calculateOptimalPercentageFontSizeSp(
            text = "37%",
            availableDiameterDp = 60f,
            resources = null,
            maxFontSizeSp = 24f,
            minFontSizeSp = 9f,
        )

        val smallSize = calculateOptimalPercentageFontSizeSp(
            text = "37%",
            availableDiameterDp = 30f,
            resources = null,
            maxFontSizeSp = 24f,
            minFontSizeSp = 9f,
        )

        assertTrue("Large diameter should give larger or equal font size", largeSize >= smallSize)
        assertTrue("Size should be >= minFontSize", smallSize >= 9f)
        assertTrue("Size should be <= maxFontSize", largeSize <= 24f)
    }

    @Test
    fun percentageFontSize_decreasesWithLongerText() {
        val shortTextSize = calculateOptimalPercentageFontSizeSp(
            text = "5%",
            availableDiameterDp = 40f,
            resources = null,
            maxFontSizeSp = 24f,
            minFontSizeSp = 9f,
        )

        val longTextSize = calculateOptimalPercentageFontSizeSp(
            text = "100%",
            availableDiameterDp = 40f,
            resources = null,
            maxFontSizeSp = 24f,
            minFontSizeSp = 9f,
        )

        assertTrue("Longer text should result in smaller or equal font size to fit", shortTextSize >= longTextSize)
    }

    @Test
    fun amountFontSize_scalesWithinBounds() {
        val amountSize = calculateOptimalAmountFontSizeSp(
            amountText = "7.50",
            availableDiameterDp = 35f,
            resources = null,
            maxFontSizeSp = 22f,
            minFontSizeSp = 10f,
        )

        assertTrue("Amount size must be within bounds", amountSize in 10f..22f)
    }

    @Test
    fun suffixFontSize_scalesWithinBounds() {
        val suffixSize = calculateOptimalSuffixFontSizeSp(
            suffixText = "GB",
            availableWidthDp = 30f,
            resources = null,
            maxFontSizeSp = 12f,
            minFontSizeSp = 8f,
        )

        assertTrue("Suffix size must be within bounds", suffixSize in 8f..12f)
    }

    @Test
    fun trafficFontSize_scalesBothAmountAndSuffix() {
        val fontSizes = calculateOptimalTrafficFontSizeSp(
            amountText = "1024.50",
            suffixText = "Gigabytes",
            availableDiameterDp = 35f,
            resources = null,
            maxAmountSp = 20f,
            minAmountSp = 8f,
            maxSuffixSp = 13f,
            minSuffixSp = 6f,
        )

        assertTrue("Amount size must be within bounds", fontSizes.amountSp in 8f..20f)
        assertTrue("Suffix size must be within bounds", fontSizes.suffixSp in 6f..13f)
        assertTrue("Amount should be larger than suffix", fontSizes.amountSp > fontSizes.suffixSp)
    }
}
