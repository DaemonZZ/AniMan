package com.daemonz.animange.datasource.network

import com.daemonz.animange.entity.ListData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface IWebService {
    @GET("home")
    suspend fun getHomeData(): Response<ListData>
    @GET("phim/{slug}")
    suspend fun getFilmData(
        @Path("slug") slug: String
    ): Response<ListData>
}