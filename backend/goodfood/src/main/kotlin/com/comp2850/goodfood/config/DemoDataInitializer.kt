package com.comp2850.goodfood.config

import com.comp2850.goodfood.diary.DiaryEntry
import com.comp2850.goodfood.diary.DiaryStore
import com.comp2850.goodfood.diary.MealType
import com.comp2850.goodfood.exercise.ExerciseEntity
import com.comp2850.goodfood.exercise.ExerciseJpaRepository
import com.comp2850.goodfood.recipes.RecipeEntity
import com.comp2850.goodfood.recipes.RecipeJpaRepository
import com.comp2850.goodfood.user.Role
import com.comp2850.goodfood.user.User
import com.comp2850.goodfood.user.repository.UserStore
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DemoDataInitializer(
    private val userStore: UserStore,
    private val passwordEncoder: PasswordEncoder,
    private val recipeJpaRepository: RecipeJpaRepository,
    private val diaryStore: DiaryStore,
    private val exerciseJpaRepository: ExerciseJpaRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        val encodedPassword = passwordEncoder.encode(DEMO_PASSWORD)
            ?: throw IllegalStateException("failed to encode demo password")

        val professional = upsertUser(
            desiredId = PRO_ID,
            name = "Dr Rivera",
            email = "dr.rivera@test.com",
            passwordHash = encodedPassword,
            role = Role.HEALTH_PROFESSIONAL,
            licence = "LIC12345",
            proId = null
        )

        val subscriber1 = upsertUser(
            desiredId = SUB_1_ID,
            name = "Alice Brown",
            email = "alice.client@test.com",
            passwordHash = encodedPassword,
            role = Role.SUBSCRIBER,
            licence = null,
            proId = professional.id
        )

        val subscriber2 = upsertUser(
            desiredId = SUB_2_ID,
            name = "Ben Carter",
            email = "ben.client@test.com",
            passwordHash = encodedPassword,
            role = Role.SUBSCRIBER,
            licence = null,
            proId = professional.id
        )

        val subscriber3 = upsertUser(
            desiredId = SUB_3_ID,
            name = "Chloe Evans",
            email = "chloe.client@test.com",
            passwordHash = encodedPassword,
            role = Role.SUBSCRIBER,
            licence = null,
            proId = professional.id
        )

        val subscriber4 = upsertUser(
            desiredId = SUB_4_ID,
            name = "David Hall",
            email = "david.client@test.com",
            passwordHash = encodedPassword,
            role = Role.SUBSCRIBER,
            licence = null,
            proId = professional.id
        )

        val subscriber5 = upsertUser(
            desiredId = SUB_5_ID,
            name = "Emma Lee",
            email = "emma.client@test.com",
            passwordHash = encodedPassword,
            role = Role.SUBSCRIBER,
            licence = null,
            proId = professional.id
        )

        seedRecipes()
        seedDiary(subscriber1.email, subscriber2.email)
        seedExercise(subscriber1.id, subscriber2.id)

        // avoid "unused variable" warnings if IDE is strict
        listOf(subscriber3, subscriber4, subscriber5)
    }

    private fun upsertUser(
        desiredId: String,
        name: String,
        email: String,
        passwordHash: String,
        role: Role,
        licence: String?,
        proId: String?
    ): User {
        val existing = userStore.findByEmail(email)

        val user = if (existing != null) {
            existing.copy(
                name = name,
                email = email,
                passwordHash = passwordHash,
                role = role,
                licence = licence,
                proId = proId
            )
        } else {
            User(
                id = desiredId,
                name = name,
                email = email,
                passwordHash = passwordHash,
                role = role,
                licence = licence,
                proId = proId
            )
        }

        return userStore.save(user)
    }

    private fun seedRecipes() {
        val existingByName = recipeJpaRepository.findAll()
            .associateBy { it.name.trim().lowercase() }
            .toMutableMap()

        val recipes = listOf(
            RecipeSeed(
                name = "Mediterranean Quinoa Bowl",
                emoji = "🥗",
                tag = "Healthy",
                kcal = 420,
                cost = "3.20",
                timeMin = 15,
                ingredients = listOf("Quinoa", "Tomato", "Cucumber", "Olive oil", "Feta"),
                steps = listOf("Cook quinoa", "Chop vegetables", "Mix everything together", "Serve")
            ),
            RecipeSeed(
                name = "Shakshuka",
                emoji = "🍳",
                tag = "Quick",
                kcal = 380,
                cost = "2.40",
                timeMin = 20,
                ingredients = listOf("Eggs", "Tomato sauce", "Onion", "Pepper"),
                steps = listOf("Cook onion and pepper", "Add tomato sauce", "Crack in eggs", "Simmer and serve")
            ),
            RecipeSeed(
                name = "Grilled Salmon & Greens",
                emoji = "🐟",
                tag = "Omega-3",
                kcal = 510,
                cost = "4.50",
                timeMin = 25,
                ingredients = listOf("Salmon", "Broccoli", "Spinach", "Olive oil"),
                steps = listOf("Season salmon", "Grill salmon", "Cook greens", "Plate and serve")
            ),
            RecipeSeed(
                name = "Avocado Toast",
                emoji = "🥑",
                tag = "Vegan",
                kcal = 290,
                cost = "1.80",
                timeMin = 10,
                ingredients = listOf("Bread", "Avocado", "Lemon", "Salt", "Pepper"),
                steps = listOf("Toast bread", "Mash avocado", "Spread on toast", "Season and serve")
            ),
            RecipeSeed(
                name = "Pasta Primavera",
                emoji = "🍝",
                tag = "Italian",
                kcal = 460,
                cost = "2.90",
                timeMin = 30,
                ingredients = listOf("Pasta", "Tomato", "Courgette", "Garlic", "Olive oil"),
                steps = listOf("Cook pasta", "Saute vegetables", "Combine with pasta", "Serve")
            ),
            RecipeSeed(
                name = "Lentil & Sweet Potato Soup",
                emoji = "🥣",
                tag = "Comfort",
                kcal = 320,
                cost = "1.60",
                timeMin = 40,
                ingredients = listOf("Lentils", "Sweet potato", "Onion", "Garlic", "Vegetable stock"),
                steps = listOf("Cook onion and garlic", "Add sweet potato and lentils", "Add stock", "Simmer and blend lightly")
            )
        )

        recipes.forEach { seed ->
            val existing = existingByName[seed.name.trim().lowercase()]

            val entity = if (existing != null) {
                existing.apply {
                    name = seed.name
                    emoji = seed.emoji
                    tag = seed.tag
                    kcal = seed.kcal
                    cost = seed.cost
                    timeMin = seed.timeMin
                    ingredients = toJsonLikeArray(seed.ingredients)
                    steps = toJsonLikeArray(seed.steps)
                }
            } else {
                RecipeEntity(
                    name = seed.name,
                    emoji = seed.emoji,
                    tag = seed.tag,
                    kcal = seed.kcal,
                    cost = seed.cost,
                    timeMin = seed.timeMin,
                    ingredients = toJsonLikeArray(seed.ingredients),
                    steps = toJsonLikeArray(seed.steps)
                )
            }

            recipeJpaRepository.save(entity)
        }
    }

    private fun seedDiary(email1: String, email2: String) {
        if (diaryStore.findByUserEmail(email1).isEmpty()) {
            val today = LocalDate.now()

            listOf(
                DiaryEntry(
                    id = 0L,
                    userEmail = email1,
                    foodName = "Banana",
                    quantity = "1 serving",
                    servings = 1.0,
                    mealType = MealType.BREAKFAST,
                    diaryDate = today.minusDays(1)
                ),
                DiaryEntry(
                    id = 0L,
                    userEmail = email1,
                    foodName = "Rice",
                    quantity = "1 serving",
                    servings = 1.0,
                    mealType = MealType.LUNCH,
                    diaryDate = today.minusDays(1)
                ),
                DiaryEntry(
                    id = 0L,
                    userEmail = email1,
                    foodName = "Milk",
                    quantity = "1 glass",
                    servings = 1.0,
                    mealType = MealType.SNACK,
                    diaryDate = today.minusDays(2)
                )
            ).forEach { diaryStore.save(it) }
        }

        if (diaryStore.findByUserEmail(email2).isEmpty()) {
            val today = LocalDate.now()

            listOf(
                DiaryEntry(
                    id = 0L,
                    userEmail = email2,
                    foodName = "Egg",
                    quantity = "2 eggs",
                    servings = 2.0,
                    mealType = MealType.BREAKFAST,
                    diaryDate = today.minusDays(3)
                ),
                DiaryEntry(
                    id = 0L,
                    userEmail = email2,
                    foodName = "Bread",
                    quantity = "2 slices",
                    servings = 2.0,
                    mealType = MealType.LUNCH,
                    diaryDate = today.minusDays(2)
                )
            ).forEach { diaryStore.save(it) }
        }
    }

    private fun seedExercise(userId1: String, userId2: String) {
        if (exerciseJpaRepository.findAllByUserIdOrderByDateDescCreatedAtDesc(userId1).isEmpty()) {
            val today = LocalDate.now()

            exerciseJpaRepository.saveAll(
                listOf(
                    ExerciseEntity(
                        userId = userId1,
                        date = today.minusDays(1),
                        activity = "Walking",
                        duration = 30,
                        kcal = 120
                    ),
                    ExerciseEntity(
                        userId = userId1,
                        date = today,
                        activity = "Cycling",
                        duration = 45,
                        kcal = 250
                    )
                )
            )
        }

        if (exerciseJpaRepository.findAllByUserIdOrderByDateDescCreatedAtDesc(userId2).isEmpty()) {
            val today = LocalDate.now()

            exerciseJpaRepository.saveAll(
                listOf(
                    ExerciseEntity(
                        userId = userId2,
                        date = today.minusDays(2),
                        activity = "Jogging",
                        duration = 20,
                        kcal = 180
                    )
                )
            )
        }
    }

    private fun toJsonLikeArray(values: List<String>): String {
        return values
            .map { "\"${it.trim().replace("\"", "\\\"")}\"" }
            .joinToString(prefix = "[", postfix = "]", separator = ",")
    }

    private data class RecipeSeed(
        val name: String,
        val emoji: String?,
        val tag: String?,
        val kcal: Int?,
        val cost: String?,
        val timeMin: Int?,
        val ingredients: List<String>,
        val steps: List<String>
    )

    companion object {
        private const val DEMO_PASSWORD = "Abcdef1!"

        private const val PRO_ID = "11111111-1111-1111-1111-111111111111"
        private const val SUB_1_ID = "22222222-2222-2222-2222-222222222221"
        private const val SUB_2_ID = "22222222-2222-2222-2222-222222222222"
        private const val SUB_3_ID = "22222222-2222-2222-2222-222222222223"
        private const val SUB_4_ID = "22222222-2222-2222-2222-222222222224"
        private const val SUB_5_ID = "22222222-2222-2222-2222-222222222225"
    }
}