package com.example.mpaz_pro_2

import com.example.mpaz_pro_2.data.local.AppRepository
import com.example.mpaz_pro_2.data.model.ObjetivoDto
import com.example.mpaz_pro_2.data.model.Post
import com.example.mpaz_pro_2.ui.viewmodel.MainViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    "MainViewModel carga datos de API externa y microservicio Spring Boot" {
        runTest {
            val fakePosts = listOf(Post(1, 1, "Aviso", "Detalle"))
            val fakeOAs = listOf(ObjetivoDto(1L, "Lenguaje", "OA01", "Lectura", "U1", "Desc", "PUBLICADO"))

            val repo = mockk<AppRepository>(relaxed = true) {
                every { allOAs } returns flowOf(emptyList())
                every { authorizedOAs } returns flowOf(emptyList())
                every { publishedOAs } returns flowOf(emptyList())
                every { allAttempts } returns flowOf(emptyList())
                every { allStudents } returns flowOf(emptyList())
                every { allReports } returns flowOf(emptyList())
                coEvery { getExternalPosts() } returns fakePosts
                coEvery { getBackendObjetivos() } returns fakeOAs
            }

            val viewModel = MainViewModel(repo)
            advanceUntilIdle()

            viewModel.postList.value shouldContainExactly fakePosts
            viewModel.backendObjetivos.value shouldContainExactly fakeOAs
        }
    }

    "addBackendObjetivo actualiza mensaje de estado al crear registro" {
        runTest {
            val repo = mockk<AppRepository>(relaxed = true) {
                every { allOAs } returns flowOf(emptyList())
                every { authorizedOAs } returns flowOf(emptyList())
                every { publishedOAs } returns flowOf(emptyList())
                every { allAttempts } returns flowOf(emptyList())
                every { allStudents } returns flowOf(emptyList())
                every { allReports } returns flowOf(emptyList())
                coEvery { getExternalPosts() } returns emptyList()
                coEvery { getBackendObjetivos() } returns emptyList()
                coEvery { createBackendObjetivo(any()) } returns ObjetivoDto(2L, "Matemática", "OA05", "Sumas", "U1", "Desc", "PUBLICADO")
            }

            val viewModel = MainViewModel(repo)
            advanceUntilIdle()

            viewModel.addBackendObjetivo("Matemática", "OA05", "Sumas", "Desc")
            advanceUntilIdle()

            viewModel.backendStatusMsg.value shouldBe "OA creado en Spring Boot"
        }
    }
})