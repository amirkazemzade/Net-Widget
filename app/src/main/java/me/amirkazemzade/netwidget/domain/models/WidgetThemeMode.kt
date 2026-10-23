package me.amirkazemzade.netwidget.domain.models

enum class WidgetThemeMode {
    OPAQUE,
    TRANSPARENT,
    ;

    companion object {
        fun valueOfOrNull(name: String?): WidgetThemeMode? =
            if (name == null) null
            else entries.find { it.name.equals(name, ignoreCase = true) }
    }
}
