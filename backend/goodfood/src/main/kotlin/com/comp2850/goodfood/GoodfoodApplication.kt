package com.comp2850.goodfood

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GoodfoodApplication

fun main(args: Array<String>) {
    runApplication<GoodfoodApplication>(*args)
}