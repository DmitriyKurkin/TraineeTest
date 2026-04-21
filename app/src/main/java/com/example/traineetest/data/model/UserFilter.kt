package com.example.traineetest.data.model

object UserFilter {

    fun filter(
        users: List<User>,
        query: String
    ): List<User> {
        if (query.isBlank()) return users

        val queryVariants = UserSearchUtils.getSearchVariants(query)

        return users.filter { user ->
            val fields = listOfNotNull(
                user.firstName,
                user.lastName,
                user.department,
                user.position,
                "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}".trim(),
                "${user.lastName.orEmpty()} ${user.firstName.orEmpty()}".trim()
            )

            val fieldVariants = fields.flatMap { fields ->
                UserSearchUtils.getSearchVariants(fields)
            }

            queryVariants.any { q ->
                fieldVariants.any { field -> field.contains(q) }
            }
        }
    }
}