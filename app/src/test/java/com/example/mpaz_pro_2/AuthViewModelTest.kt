package com.example.mpaz_pro_2

import com.example.mpaz_pro_2.data.local.AppRepository
import com.example.mpaz_pro_2.data.local.UserEntity
import com.example.mpaz_pro_2.ui.viewmodel.AuthViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    "login falla con campos vacíos y triunfa con credenciales válidas" {
        runTest {
            val repo = mockk<AppRepository>(relaxed = true)
            val validUser = UserEntity(username = "profe1", name = "Profesor", password = "1234", role = "DOCENTE")
            coEvery { repo.getUserByUsername("profe1") } returns validUser

            val vm = AuthViewModel(repo)
            vm.login {}
            vm.uiState.value.usernameError shouldNotBe null

            var loggedUser: UserEntity? = null
            vm.onUsernameChanged("profe1")
            vm.onPasswordChanged("1234")
            vm.login { loggedUser = it }
            advanceUntilIdle()

            loggedUser?.username shouldBe "profe1"
        }
    }
})