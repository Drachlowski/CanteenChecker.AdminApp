package com.example.canteenchecker.adminapp.core

interface AdminApi {
    suspend fun authenticate(username: String, password: String): Result<String>
    suspend fun getCanteen(authenticationToken: String): Result<Canteen>
    suspend fun getCanteenReviews(authenticationToken: String): Result<List<CanteenReview>>
    suspend fun getCanteenStatistics(authenticationToken: String): Result<ReviewStatistic>
    suspend fun updateCanteenData(authenticationToken: String, canteenData: CanteenData): Result<Unit>
    suspend fun updateDish(authenticationToken: String, dish: Dish): Result<Unit>
    suspend fun updateWaitingTime(authenticationToken: String, waitingTime: Int): Result<Unit>
    suspend fun deleteReview(authenticationToken: String, reviewId: String): Result<Unit>
}