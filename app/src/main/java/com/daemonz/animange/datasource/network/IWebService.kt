package com.daemonz.animange.datasource.network

import com.daemonz.animange.entity.Home
import retrofit2.Response
import retrofit2.http.GET

interface IWebService {
    @GET("home")
    suspend fun getHomeData(): Response<Home>
}