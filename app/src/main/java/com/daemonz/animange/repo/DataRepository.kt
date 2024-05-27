package com.daemonz.animange.repo

import com.daemonz.animange.base.NetworkEntity
import com.daemonz.animange.datasource.network.IWebService
import com.daemonz.animange.entity.Item
import com.daemonz.animange.entity.ListData
import retrofit2.Response

class DataRepository(
     private val apiService: IWebService,
) {

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
     suspend fun loadPlayerData(slug: String): ListData {
          return handleDataResponse(apiService.getFilmData(slug))
     }

}