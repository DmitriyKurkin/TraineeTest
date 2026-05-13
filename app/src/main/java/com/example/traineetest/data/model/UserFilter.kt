package com.example.traineetest.data.model

object UserFilter {

    fun filter(
        users: List<User>,
        query: String
    ): List<User> {
        if (query.isBlank()) return users

        val queryVariants = UserSearchUtils.getSearchVariants(query)

        return users.filter { user ->
            val fields = listOf(
                user.firstName,
                user.lastName,
                user.userTag.removePrefix("@"),
                "${user.firstName} ${user.lastName}",
                "${user.lastName} ${user.firstName}"
            )

            val fieldVariants = fields.flatMap { field ->
                UserSearchUtils.getSearchVariants(field)
            }

            queryVariants.any { queryVariant ->
                fieldVariants.any { fieldVariant ->
                    fieldVariant.contains(queryVariant)
                }
            }
        }
    }
}