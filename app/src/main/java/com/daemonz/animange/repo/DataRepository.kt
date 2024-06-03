package com.daemonz.animange.repo

import com.daemonz.animange.base.NetworkEntity
import com.daemonz.animange.datasource.network.IWebService
import com.daemonz.animange.datasource.room.FavouriteDao
import com.daemonz.animange.entity.FavouriteItem
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.ListData
import com.daemonz.animange.util.Country
import com.daemonz.animange.util.TypeList
import com.daemonz.animange.util.toFavouriteItem
import retrofit2.Response

class DataRepository(
     private val apiService: IWebService,
     private val dao: FavouriteDao
) {
     companion object {
          private const val TAG = "DataRepository"
     }

     private  fun <T:NetworkEntity> handleDataResponse(response: Response<T>):T {
          return if(response.isSuccessful && response.body() != null) {
               response.body()!!
          } else if(!response.isSuccessful && response.errorBody()!= null) {
               (NetworkEntity() as T).apply { error = response.errorBody()?.string() }
          } else {
               NetworkEntity() as T
          }
     }
     suspend fun getHomeData(): ListData {
          return handleDataResponse(apiService.getHomeData())
     }
     suspend fun getNewFilms(): ListData {
          return handleDataResponse(apiService.getNewFilms())
     }

     suspend fun getSeriesInComing(): ListData {
          return handleDataResponse(apiService.getSeriesInComing())
     }
     suspend fun getListAnime(): ListData {
          return handleDataResponse(apiService.filterData(list = "hoat-hinh", country = "nhat-ban"))
     }

     suspend fun getListMovies(): ListData {
          return handleDataResponse(apiService.getMovies())
     }
     suspend fun getTvShows(): ListData {
          return handleDataResponse(apiService.getTvShows())
     }
     suspend fun getAllSeries(): ListData {
          return handleDataResponse(apiService.getAllSeries())
     }
     suspend fun loadPlayerData(slug: String): ListData {
          return handleDataResponse(apiService.getFilmData(slug))
     }

     suspend fun get24RelatedFilm(slug: String, category: String): ListData {
          val data = apiService.filterData(
               list = slug,
               category = category
          )
          return handleDataResponse(data)
     }
     suspend fun getListFilmVietNam(): ListData {
          val data = apiService.filterData(
               list = TypeList.Movie.value,
               country = Country.VietNam.value,
          )
          return handleDataResponse(data)
     }
    suspend fun searchFilm(query: String): ListData {
        return handleDataResponse(apiService.search(query))
    }

     fun markItemAsFavourite(item: Item, img: String) {
          dao.insertAll(item.toFavouriteItem(img))
     }

     fun unMarkItemAsFavourite(slug: String) {
          dao.delete(slug)
     }

     fun isItemFavourite(slug: String): Boolean {
          return dao.loadAllBySlug(slug).isNotEmpty()
     }

     fun getAllFavourite(): List<FavouriteItem> {
          return dao.getAll()
     }
}