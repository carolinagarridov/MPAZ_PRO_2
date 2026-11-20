package com.example.mpaz_pro_2.data.remote

import com.example.mpaz_pro_2.data.model.ObjetivoDto
import retrofit2.Response
import retrofit2.http.*

interface BackendApiService {
    @GET("api/objetivos")
    suspend fun getObjetivos(): List<ObjetivoDto>

    @POST("api/objetivos")
    suspend fun createObjetivo(@Body objetivo: ObjetivoDto): ObjetivoDto

    @PUT("api/objetivos/{id}")
    suspend fun updateObjetivo(@Path("id") id: Long, @Body objetivo: ObjetivoDto): ObjetivoDto

    @DELETE("api/objetivos/{id}")
    suspend fun deleteObjetivo(@Path("id") id: Long): Response<Unit>
}