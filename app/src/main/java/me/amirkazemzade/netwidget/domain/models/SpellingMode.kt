package me.amirkazemzade.netwidget.domain.models

enum class SpellingMode {
    Full,
    Short;

    companion object {
        fun valueOfOrNull(value: String): SpellingMode? {
            return runCatching {
                SpellingMode.valueOf(value = value)
            }.getOrNull()
        }
    }

}