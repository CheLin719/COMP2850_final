package com.comp2850.goodfood.recipes

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class RecipeDataInitializer(
    private val recipeJpaRepository: RecipeJpaRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (recipeJpaRepository.count() > 0) return

        recipeJpaRepository.saveAll(
            listOf(
                RecipeEntity(
                    name = "Mediterranean Quinoa Bowl",
                    emoji = "🥗",
                    tag = "Healthy",
                    kcal = 420,
                    cost = "3.20",
                    timeMin = 15,
                    ingredients = """["Quinoa","Tomato","Cucumber","Olive oil","Feta"]""",
                    steps = """["Cook quinoa","Chop vegetables","Mix everything together","Serve"]"""
                ),
                RecipeEntity(
                    name = "Shakshuka",
                    emoji = "🍳",
                    tag = "Quick",
                    kcal = 380,
                    cost = "2.40",
                    timeMin = 20,
                    ingredients = """["Eggs","Tomato sauce","Onion","Pepper"]""",
                    steps = """["Cook onion and pepper","Add tomato sauce","Crack in eggs","Simmer and serve"]"""
                ),
                RecipeEntity(
                    name = "Grilled Salmon & Greens",
                    emoji = "🐟",
                    tag = "Omega-3",
                    kcal = 510,
                    cost = "4.50",
                    timeMin = 25,
                    ingredients = """["Salmon","Broccoli","Spinach","Olive oil"]""",
                    steps = """["Season salmon","Grill salmon","Cook greens","Plate and serve"]"""
                ),
                RecipeEntity(
                    name = "Avocado Toast",
                    emoji = "🥑",
                    tag = "Vegan",
                    kcal = 290,
                    cost = "1.80",
                    timeMin = 10,
                    ingredients = """["Bread","Avocado","Lemon","Salt","Pepper"]""",
                    steps = """["Toast bread","Mash avocado","Spread on toast","Season and serve"]"""
                ),
                RecipeEntity(
                    name = "Pasta Primavera",
                    emoji = "🍝",
                    tag = "Italian",
                    kcal = 460,
                    cost = "2.90",
                    timeMin = 30,
                    ingredients = """["Pasta","Tomato","Courgette","Garlic","Olive oil"]""",
                    steps = """["Cook pasta","Saute vegetables","Combine with pasta","Serve"]"""
                ),
                RecipeEntity(
                    name = "Lentil & Sweet Potato Soup",
                    emoji = "🥣",
                    tag = "Comfort",
                    kcal = 320,
                    cost = "1.60",
                    timeMin = 40,
                    ingredients = """["Lentils","Sweet potato","Onion","Garlic","Vegetable stock"]""",
                    steps = """["Cook onion and garlic","Add sweet potato and lentils","Add stock","Simmer and blend lightly"]"""
                )
            )
        )
    }
}