package com.example.mpaz_pro_2.data.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SecurityUtils {

    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256

    fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun hashPassword(password: String, saltBase64: String): String {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun verifyPassword(password: String, saltBase64: String, hashToMatch: String): Boolean {
        val calculated = hashPassword(password, saltBase64)
        // Comparación en tiempo constante
        return MessageDigest.isEqual(calculated.toByteArray(), hashToMatch.toByteArray())
    }
}