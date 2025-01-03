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
)

class CanteenReview(
    val id: String,
    val creationDate: String,
    val creator: String,
    val rating: Int,
    val remark: String
)