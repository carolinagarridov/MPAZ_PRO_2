package com.example.mpaz_pro_2.data.remote

import com.example.mpaz_pro_2.data.model.Post
import retrofit2.http.GET

interface ApiService {
    @GET("/posts")
    suspend fun getPosts(): List<Post>
}