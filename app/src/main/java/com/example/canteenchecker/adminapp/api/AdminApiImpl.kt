package com.example.canteenchecker.adminapp.api

import android.util.Log
import com.example.canteenchecker.adminapp.core.AdminApi
import com.example.canteenchecker.adminapp.core.Canteen
import com.example.canteenchecker.adminapp.core.CanteenReview
import com.example.canteenchecker.adminapp.core.ReviewStatistic
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.IOException

object AdminApiFactory {
    fun createAdminApi(): AdminApi =
        AdminApiImpl("https://moc5.projekte.fh-hagenberg.at/CanteenChecker/api/")
}

private class AdminApiImpl(apiBaseUrl: String) : AdminApi {
    private val retrofit = Retrofit.Builder().baseUrl(apiBaseUrl).addConverterFactory(
        ScalarsConverterFactory.create()
    ).addConverterFactory(GsonConverterFactory.create()).build()

    override suspend fun authenticate(username: String, password: String): Result<String> =
        apiCall { postAuthenticate(username, password) }

    override suspend fun getCanteen(): Result<Canteen> {
        return Result.success(Canteen(
            "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            "Holzhütte",
            "Softwarepark 21, 4232 Hagenberg im Mühlkreis",
            "+43 664 79 56 151",
            "w.at",
            "Hackschnitzel",
            9.99,
            512
            ))
    }

    override suspend fun getCanteenReviews(): Result<List<CanteenReview>> {
        return Result.success(listOf(
            CanteenReview(
                id = "e2a26d2f-aa2c-47f9-bc65-bb48f1145050",
                creationDate = "2025-01-03T00:40:01.9479971",
                creator = "S2210307030",
                rating = 5,
                remark = "Ausgezeichnetes Bier"
            )
        ))
    }

    override suspend fun getCanteenStatistics(): Result<ReviewStatistic> {
        return Result.success(ReviewStatistic(
            countOneStar = 0,
            countTwoStars = 0,
            countThreeStars = 0,
            countFourStars = 0,
            countFiveStars = 1
        ))
    }

    private interface Api {
        @POST("authenticate")
        suspend fun postAuthenticate(
            @Query("userName") username: String, @Query("password") password: String
        ): String
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