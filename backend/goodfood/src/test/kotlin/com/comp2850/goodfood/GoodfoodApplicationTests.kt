package com.comp2850.goodfood

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GoodfoodApplicationTests {

	@Test
	fun contextLoads() {
	}

}

@SpringBootTest(
    properties = [
        "jwt.secret=test-secret-key-for-ci-only-please-change-in-production-123456789",
        "jwt.expiration=3600000"
    ]
)
class GoodfoodApplicationTests {

    @Test
    fun contextLoads() {
    }
}
