package com.example.mpaz_pro_2

import com.example.mpaz_pro_2.model.ValidationEngine
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ValidationEngineTest : StringSpec({
    "validateName rechaza vacíos o cortos y acepta nombres válidos" {
        ValidationEngine.validateName("") shouldNotBe null
        ValidationEngine.validateName("Ab") shouldNotBe null
        ValidationEngine.validateName("Gabriela Mistral") shouldBe null
    }

    "validateUsername rechaza espacios y longitudes menores a 4" {
        ValidationEngine.validateUsername("") shouldNotBe null
        ValidationEngine.validateUsername("abc") shouldNotBe null
        ValidationEngine.validateUsername("juan perez") shouldNotBe null
        ValidationEngine.validateUsername("docente1") shouldBe null
    }

    "validatePassword y confirmación validan reglas de seguridad" {
        ValidationEngine.validatePassword("123") shouldNotBe null
        ValidationEngine.validatePassword("clave123") shouldBe null
        ValidationEngine.validatePasswordConfirmation("clave123", "otra") shouldNotBe null
        ValidationEngine.validatePasswordConfirmation("clave123", "clave123") shouldBe null
    }
})