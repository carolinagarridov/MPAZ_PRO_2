package com.example.mpaz_pro_2

import com.example.mpaz_pro_2.data.local.*
import com.example.mpaz_pro_2.data.model.ObjetivoDto
import com.example.mpaz_pro_2.data.model.Post
import com.example.mpaz_pro_2.data.remote.ApiService
import com.example.mpaz_pro_2.data.remote.BackendApiService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import retrofit2.Response

class AppRepositoryTest : StringSpec({
    val userDao = mockk<UserDao>(relaxed = true)
    val curricularDao = mockk<CurricularDao>(relaxed = true)
    val questionDao = mockk<QuestionDao>(relaxed = true)
    val configDao = mockk<EvaluationConfigDao>(relaxed = true)
    val attemptDao = mockk<AttemptDao>(relaxed = true)
    val reportDao = mockk<ReportDao>(relaxed = true)
    val externalApi = mockk<ApiService>()
    val backendApi = mockk<BackendApiService>()

    val repository = AppRepository(
        userDao, curricularDao, questionDao, configDao, attemptDao, reportDao, externalApi, backendApi
    )

    "getExternalPosts retorna circulares simuladas" {
        val posts = listOf(Post(1, 1, "Circular", "Detalle"))
        coEvery { externalApi.getPosts() } returns posts
        repository.getExternalPosts() shouldContainExactly posts
    }

    "operaciones CRUD de backend delegan en BackendApiService" {
        val oa = ObjetivoDto(1L, "Matemática", "OA02", "Conteo", "Unidad 1", "Desc", "PUBLICADO")
        coEvery { backendApi.getObjetivos() } returns listOf(oa)
        coEvery { backendApi.createObjetivo(any()) } returns oa
        coEvery { backendApi.updateObjetivo(1L, any()) } returns oa.copy(status = "AUTORIZADO")
        coEvery { backendApi.deleteObjetivo(1L) } returns Response.success(Unit)

        repository.getBackendObjetivos() shouldContainExactly listOf(oa)
        repository.createBackendObjetivo(oa) shouldBe oa
        repository.updateBackendObjetivo(1L, oa).status shouldBe "AUTORIZADO"
        repository.deleteBackendObjetivo(1L) shouldBe true
    }

    "operaciones Room delegan inserción de usuario" {
        val user = UserEntity(username = "alu1", name = "Alumno Uno", password = "1234", role = "ESTUDIANTE")
        coEvery { userDao.insertUser(any()) } just Runs
        repository.insertUser(user)
        coVerify { userDao.insertUser(user) }
    }
})