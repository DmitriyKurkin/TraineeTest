package com.example.traineetest.data.model

object UserSearchUtils {

    fun getSearchVariants(text: String?): List<String> {
        if (text.isNullOrBlank()) return emptyList()

        val normalized = text
            .trim()
            .lowercase()

        val variants = linkedSetOf<String>()
        variants.add(normalized)
        variants.add(translitRuToEn(normalized))
        variants.add(translitEnToRu(normalized))

        return variants
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    private fun translitRuToEn(text: String): String {
        val map = mapOf(
            'а' to "a",  'б' to "b",  'в' to "v",  'г' to "g",
            'д' to "d",  'е' to "e",  'ё' to "yo", 'ж' to "zh",
            'з' to "z",  'и' to "i",  'й' to "y",  'к' to "k",
            'л' to "l",  'м' to "m",  'н' to "n",  'о' to "o",
            'п' to "p",  'р' to "r",  'с' to "s",  'т' to "t",
            'у' to "u",  'ф' to "f",  'х' to "kh", 'ц' to "ts",
            'ч' to "ch", 'ш' to "sh", 'щ' to "sch",'ъ' to "",
            'ы' to "y",  'ь' to "",   'э' to "e",  'ю' to "yu",
            'я' to "ya"
        )

        return buildString {
            for (char in text) {
                append(map[char] ?: char)
            }
        }
    }

    private fun translitEnToRu(text: String): String {
        var result = text

        val multiMap = listOf(
            "sch" to "щ",
            "yo" to "ё",
            "zh" to "ж",
            "kh" to "х",
            "ts" to "ц",
            "ch" to "ч",
            "sh" to "ш",
            "yu" to "ю",
            "ya" to "я"
        )

        for ((latin, cyrillic) in multiMap) {
            result = result.replace(latin, cyrillic)
        }

        val singleMap = mapOf(
            'a' to 'а', 'b' to 'б', 'v' to 'в', 'g' to 'г',
            'd' to 'д', 'e' to 'е', 'z' to 'з', 'i' to 'и',
            'y' to 'й', 'k' to 'к', 'l' to 'л', 'm' to 'м',
            'n' to 'н', 'o' to 'о', 'p' to 'п', 'r' to 'р',
            's' to 'с', 't' to 'т', 'u' to 'у', 'f' to 'ф',
            'h' to 'х', 'c' to 'к', 'j' to "дж", 'q' to 'к',
            'w' to 'в', 'x' to "кс"
        )

        return buildString {
            for (char in result) {
                append(singleMap[char] ?: char.toString())
            }
        }
    }
}