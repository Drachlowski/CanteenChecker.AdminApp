package com.example.canteenchecker.adminapp.core

class Canteen(
    val id: String,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val website: String,
    val dish: String,
    val dishPrice: Double,
    val waitingTime: Int
)

class ReviewStatistic(
    val countOneStar: Int,
    val countTwoStars: Int,
    val countThreeStars: Int,
    val countFourStars: Int,
    val countFiveStars: Int
) {
    val totalRatings = countOneStar + countTwoStars + countThreeStars + countFourStars + countFiveStars
    val averageRating =
        if (totalRatings == 0) 0f
        else (countOneStar + (countTwoStars * 2) + (countThreeStars * 3) + (countFourStars * 4) + (countFiveStars * 5)) / totalRatings.toFloat()
}

class CanteenReview(
    val id: String,
    val creationDate: String,
    val creator: String,
    val rating: Int,
    val remark: String
)

class CanteenData(
    val name: String,
    val address: String,
    val website: String,
    val phoneNumber: String
)

class Dish(
    val dish: String,
    val dishPrice: Double
)