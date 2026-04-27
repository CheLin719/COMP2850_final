package com.comp2850.goodfood.auth.validation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PasswordValidatorTest {

    @Test
    fun `valid password returns true`() {
        assertTrue(PasswordValidator.isValid("StrongPass1!"))
    }

    @Test
    fun `password too short returns false`() {
        assertFalse(PasswordValidator.isValid("Ab1!"))
    }

    @Test
    fun `password missing uppercase returns false`() {
        assertFalse(PasswordValidator.isValid("weakpass1!"))
    }

    @Test
    fun `password missing lowercase returns false`() {
        assertFalse(PasswordValidator.isValid("WEAKPASS1!"))
    }

    @Test
    fun `password missing digit returns false`() {
        assertFalse(PasswordValidator.isValid("WeakPass!"))
    }

    @Test
    fun `password missing special char returns false`() {
        assertFalse(PasswordValidator.isValid("WeakPass123"))
    }
}
