package com.example.traineetest.data.model

object UserFilter {

    fun filter(
        User: List<User>,
        query: String
    ): List<User> {
        if (query.isBlank()) return User

        val queryVariants = UserSearchUtils.getSearchVariants(query)

        return User.filter { user ->
            val fields = listOf(
                user.firstName,
                user.lastName,
                user.department,
                user.position,
                "${user.firstName} ${user.lastName}",
                "${user.lastName} ${user.firstName}"
            )

            val fieldVariants = fields.flatMap {
                UserSearchUtils.getSearchVariants(it)
            }

            queryVariants.any { q ->
                fieldVariants.any { field -> field.contains(q) }
            }
        }
    }
}