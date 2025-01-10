package com.example.canteenchecker.adminapp.api

import android.util.Log
import com.example.canteenchecker.adminapp.core.AdminApi
import com.example.canteenchecker.adminapp.core.Canteen
import com.example.canteenchecker.adminapp.core.CanteenData
import com.example.canteenchecker.adminapp.core.CanteenReview
import com.example.canteenchecker.adminapp.core.Dish
import com.example.canteenchecker.adminapp.core.ReviewStatistic
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.io.IOException

object AdminApiFactory {
    fun createAdminApi(): AdminApi =
        AdminApiImpl("https://moc5.projekte.fh-hagenberg.at/CanteenChecker/api/admin/")
}

private class AdminApiImpl(apiBaseUrl: String) : AdminApi {
    private val retrofit = Retrofit.Builder().baseUrl(apiBaseUrl).addConverterFactory(
        ScalarsConverterFactory.create()
    ).addConverterFactory(GsonConverterFactory.create()).build()

    override suspend fun authenticate(username: String, password: String): Result<String> =
        apiCall { postAuthenticate(username, password) }

    override suspend fun getCanteen(authenticationToken: String): Result<Canteen> =
        apiCall {
            getCanteen("Bearer $authenticationToken")
        }.convert{ Canteen(id, name, address, phoneNumber, website, dish, dishPrice, waitingTime) }

    override suspend fun getCanteenReviews(authenticationToken: String): Result<List<CanteenReview>> =
        apiCall {
            getCanteenReviews("Bearer $authenticationToken")
        }.convertEach { CanteenReview(id, creationDate, creator, rating, remark) }

    override suspend fun getCanteenStatistics(authenticationToken: String): Result<ReviewStatistic> =
        apiCall {
            getCanteenReviewStatistics("Bearer $authenticationToken")
        }.convert{ ReviewStatistic(countOneStar, countTwoStars, countThreeStars, countFourStars, countFiveStars) }

    override suspend fun updateCanteenData(authenticationToken: String, canteenData: CanteenData): Result<Unit> =
        apiCall {
            putCanteenData("Bearer $authenticationToken", canteenData.name, canteenData.address, canteenData.website, canteenData.phoneNumber)
        }.convert {  }

    override suspend fun updateDish(authenticationToken: String, dish: Dish): Result<Unit> =
        apiCall {
            putDishOfTheDay("Bearer $authenticationToken", dish.dish, dish.dishPrice)
        }.convert {  }

    override suspend fun updateWaitingTime(authenticationToken: String, waitingTime: Int): Result<Unit> =
        apiCall {
            putWaitingTime("Bearer $authenticationToken", waitingTime)
        }.convert {  }

    override suspend fun deleteReview(authenticationToken: String, reviewId: String): Result<Unit> =
        apiCall {
            deleteCanteenReview("Bearer $authenticationToken", reviewId)
        }.convert {  }

    private interface Api {
        @POST("authenticate")
        suspend fun postAuthenticate(
            @Query("userName") username: String,
            @Query("password") password: String
        ): String

        @GET("canteen")
        suspend fun getCanteen(
            @Header("Authorization") authenticationToken: String
        ): Canteen

        @GET("canteen/review-statistics")
        suspend fun getCanteenReviewStatistics(
            @Header("Authorization") authenticationToken: String
        ): ReviewStatistic

        @PUT("canteen/data")
        suspend fun putCanteenData(
            @Header("Authorization") authenticationToken: String,
            @Query("name") name: String,
            @Query("address") address: String,
            @Query("website") website: String,
            @Query("phoneNumber") phoneNumber: String
        ): Response<Unit>

        @PUT("canteen/dish")
        suspend fun putDishOfTheDay(
            @Header("Authorization") authenticationToken: String,
            @Query("dish") dish: String,
            @Query("dishPrice") dishPrice: Double
        ): Response<Unit>

        @PUT("canteen/waiting-time")
        suspend fun putWaitingTime(
            @Header("Authorization") authenticationToken: String,
            @Query("waitingTime") waitingTime: Int
        ): Response<Unit>

        @GET("canteen/reviews")
        suspend fun getCanteenReviews(
            @Header("Authorization") authenticationToken: String
        ): List<CanteenReview>

        @DELETE("canteen/reviews/{reviewId}")
        suspend fun deleteCanteenReview(
            @Header("Authorization") authenticationToken: String,
            @Path("reviewId") reviewId: String
        ): Response<Unit>
    }

    private inline fun <T> apiCall(call: Api.() -> T): Result<T> = try {
        Result.success(call(retrofit.create()))
    } catch (ex: HttpException) {
        Result.failure(ex)
    } catch (ex: IOException) {
        Result.failure(ex)
    }.onFailure { Log.e(TAG, "API call failed", it) }

    companion object {
        private val TAG = this::class.simpleName
    }
}

private inline fun <T, R> Result<List<T>>.convertEach(map: T.() -> R): Result<List<R>> =
    this.map { it.map(map) }

private inline fun <T, R> Result<T>.convert(map: T.() -> R): Result<R> = this.map(map)