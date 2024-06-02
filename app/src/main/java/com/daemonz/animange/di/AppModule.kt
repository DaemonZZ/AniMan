package com.daemonz.animange.di

import com.daemonz.animange.BuildConfig
import com.daemonz.animange.datasource.firebase.FireBaseDataBase
import com.daemonz.animange.datasource.network.IWebService
import com.daemonz.animange.repo.DataRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideHandler() = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    @Singleton
    @Provides
    fun provideIOScope(handler: CoroutineExceptionHandler) = CoroutineScope(
        Dispatchers.IO +
                SupervisorJob() +
                CoroutineName("IO_Scope") +
                handler
    )

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create()
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .connectTimeout(200, TimeUnit.SECONDS)
            .readTimeout(200, TimeUnit.SECONDS).addInterceptor(logging).build()
    }

    @Provides
    @Singleton
    fun provideWebService(gson: Gson, client: OkHttpClient): IWebService {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .build()
            .create(IWebService::class.java)
    }
    @Provides
    @Singleton
    fun provideDataRepository(
        webApi: IWebService,
    ): DataRepository {
        return DataRepository(webApi)
    }

    @Provides
    @Singleton
    fun provideFireBaseFireStore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideFireBaseDataBase(
        fireBaseFireStore: FirebaseFirestore,
    ): FireBaseDataBase {
        return FireBaseDataBase(fireBaseFireStore)
    }

//    @Singleton
//    @Provides
//    fun provideDao(db: AppDatabase): PlayListDao {
//        return db.getDao()
//    }

//    @Provides
//    @Singleton
//    fun provideDatabase(
//        @ApplicationContext appContext: Context,
//        scope: CoroutineScope,
//        daoProvider: Provider<PlayListDao>
//    ): AppDatabase {
//        return Room.databaseBuilder(appContext, AppDatabase::class.java, "app.db")
//            .addCallback(object : RoomDatabase.Callback() {
//                override fun onCreate(db: SupportSQLiteDatabase) {
//                    super.onCreate(db)
//                    scope.launch {
//                        initData(daoProvider.get())
//                    }
//                }
//            })
//            .build()
//    }

//
//    private suspend fun initData(dao: PlayListDao) {
//        val favorite = TrackListInfo(PlayListDao.ID_LIST_FAVORITE, "Favorite")
//        val history = TrackListInfo(PlayListDao.ID_LIST_HISTORY, "History")
//        dao.insertTrackList(favorite)
//        dao.insertTrackList(history)
//    }
}
