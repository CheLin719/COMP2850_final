package com.comp2850.goodfood.auth.validation

object PasswordValidator {
    fun isValid(password: String): Boolean {
        // 1. Minimum length of 8 characters
        if (password.length < 8) return false

        // 2. Must contain: uppercase, lowercase, digit, and special character
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }
}
