package com.example.canteenchecker.adminapp.core

interface AdminApi {
    suspend fun authenticate(username: String, password: String): Result<String>
    suspend fun getCanteen(): Result<Canteen>
    suspend fun getCanteenReviews(): Result<List<CanteenReview>>
    suspend fun getCanteenStatistics(): Result<ReviewStatistic>
}