package com.example.traineetest.data.model

object UserSearchUtils {

    fun normalize(text: String): String {
        return text.lowercase().trim()
    }

    fun transliterateRuToLat(text: String): String {
        val map = mapOf(
            'а' to "a", 'б' to "b", 'в' to "v", 'г' to "g",
            'д' to "d", 'е' to "e", 'ё' to "e", 'ж' to "zh",
            'з' to "z", 'и' to "i", 'й' to "y", 'к' to "k",
            'л' to "l", 'м' to "m", 'н' to "n", 'о' to "o",
            'п' to "p", 'р' to "r", 'с' to "s", 'т' to "t",
            'у' to "u", 'ф' to "f", 'х' to "h", 'ц' to "ts",
            'ч' to "ch", 'ш' to "sh", 'щ' to "sch",
            'ъ' to "", 'ы' to "y", 'ь' to "", 'э' to "e",
            'ю' to "yu", 'я' to "ya"
        )

        return text.lowercase().map { map[it] ?: it.toString() }.joinToString("")
    }

    fun getSearchVariants(text: String): List<String> {
        val normalized = normalize(text)
        val transliterated = transliterateRuToLat(normalized)
        return listOf(normalized, transliterated).distinct()
    }
}