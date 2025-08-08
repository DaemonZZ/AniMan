package com.daemonz.animange.datasource.network

import com.daemonz.animange.entity.ListData
import com.daemonz.animange.entity.manga.ListDataManga
import com.daemonz.animange.util.Category
import com.daemonz.animange.util.SortField
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IMangaService {
    @GET("home")
    suspend fun getHomeManga(): Response<ListDataManga>

    @GET("danh-sach/{type}")
    suspend fun getListManga(
        @Path("type") type: String,
        @Query("page") page: String = ""
    ): Response<ListData>

    @GET("the-loai/{slug}")
    suspend fun getCategoryBySlug(
        @Path("slug") slug: String,
        @Query("page") page: String = ""
    )

    @GET("truyen-tranh/{slug}")
    suspend fun getMangaBySlug(
        @Path("slug") slug: String
    ): Response<ListDataManga>

    //https://otruyenapi.com/v1/api/tim-kiem?keyword={keyword}&page={page}
    @GET("tim-kiem")
    suspend fun searchManga(
        @Query("keyword") keyword: String,
        @Query("page") page: String = ""
    )
}