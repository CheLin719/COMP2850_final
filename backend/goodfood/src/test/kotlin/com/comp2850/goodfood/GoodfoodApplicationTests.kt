package com.comp2850.goodfood

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "jwt.secret=test-secret-key-for-ci-only-please-change-in-production-123456789",
        "jwt.expiration=3600000",
        "jwt.expiration-ms=3600000",
        "jwt.expirationMs=3600000",
        "app.jwt.secret=test-secret-key-for-ci-only-please-change-in-production-123456789",
        "app.jwt.expiration=3600000"
    ]
)
class GoodfoodApplicationTests {

    @Test
    fun contextLoads() {
    }
}
