package com.example.mpaz_pro_2.data.model

data class ObjetivoDto(
    val id: Long? = null,
    val subject: String,
    val oaCode: String,
    val oaTitle: String,
    val unit: String = "Unidad 1",
    val description: String,
    val status: String = "PUBLICADO"
)