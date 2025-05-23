package com.daemonz.animange.datasource.network

import com.daemonz.animange.entity.ListData
import com.daemonz.animange.util.Category
import com.daemonz.animange.util.SortField
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IWebService {
    @GET("home")
    suspend fun getHomeData(): Response<ListData>
    @GET("phim/{slug}")
    suspend fun getFilmData(
        @Path("slug") slug: String
    ): Response<ListData>

    @GET("danh-sach/{list}")
    suspend fun filterData(
        @Path("list") list: String,
        @Query("sort_field") sortBy: String = SortField.LastUpdated.value,
        @Query("category") category: String = Category.All.value,
        @Query("country") country: String = "",
        @Query("year") year: String = "",
    ): Response<ListData>

    @GET("danh-sach/phim-moi")
    suspend fun getNewFilms(): Response<ListData>

    @GET("danh-sach/phim-bo-dang-chieu")
    suspend fun getSeriesInComing(): Response<ListData>

    @GET("danh-sach/hoat-hinh")
    suspend fun getListAnime(): Response<ListData>

    @GET("danh-sach/phim-le")
    suspend fun getMovies(): Response<ListData>

    @GET("danh-sach/tv-shows")
    suspend fun getTvShows(): Response<ListData>

    @GET("danh-sach/phim-bo")
    suspend fun getAllSeries(): Response<ListData>

    @GET("tim-kiem")
    suspend fun search(
        @Query("keyword") keyword: String
    ): Response<ListData>
}