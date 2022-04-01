package com.psyma17.healthyweightapplication.data

data class UserProfileData(
    var uid: String = "",
    var userName: String = "",
    var objective: String = "lose",
    var meetFriend: Boolean = false,
    var public: Boolean = true,
    var currentWeight: Double = 0.0,
    var aboutMe: String = "This user has not written an about me yet.",
)
