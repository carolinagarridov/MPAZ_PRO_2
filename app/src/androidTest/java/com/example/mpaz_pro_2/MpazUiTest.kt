package com.example.mpaz_pro_2

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.mpaz_pro_2.data.model.Post
import com.example.mpaz_pro_2.ui.screens.CircularesMineducSection
import org.junit.Rule
import org.junit.Test

class MpazUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun verificarDespliegueDeCircularesEnPantalla() {
        val samplePosts = listOf(
            Post(userId = 1, id = 1, title = "Circular Oficial Escuela", body = "Inicio de evaluaciones")
        )

        composeRule.setContent {
            CircularesMineducSection(posts = samplePosts)
        }

        composeRule.onNodeWithText("📢 Circulares y Comunicados Externos (API REST)").assertIsDisplayed()
        composeRule.onNodeWithText("Circular Oficial Escuela").assertIsDisplayed()
        composeRule.onNodeWithText("Inicio de evaluaciones").assertIsDisplayed()
    }
}