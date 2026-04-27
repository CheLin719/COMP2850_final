package com.comp2850.goodfood.auth.validation

object PasswordValidator {
    fun isValid(password: String): Boolean {
        // 1. 長度至少 8 位
        if (password.length < 8) return false
        
        // 2. 檢查是否包含：大寫、小寫、數字、特殊字符
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }
}
