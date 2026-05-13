package com.example.traineetest.data.model


enum class Department(
    val apiValue: String,
    val displayName: String
) {
    ALL("all", "Все"),
    ANDROID("android", "Android"),
    IOS("ios", "iOS"),
    DESIGN("design", "Дизайн"),
    MANAGEMENT("management", "Менеджмент"),
    QA("qa", "QA"),
    BACK_OFFICE("back_office", "Бэк-офис"),
    FRONTEND("frontend", "Frontend"),
    HR("hr", "HR"),
    PR("pr", "PR"),
    BACKEND("backend", "Backend"),
    SUPPORT("support", "Техподдержка"),
    ANALYTICS("analytics", "Аналитика");

    companion object {
        val visibleTabs: List<Department> = entries

        fun fromApiValue(value: String): Department {
            return entries.firstOrNull { it.apiValue == value } ?: ALL
        }
    }
}