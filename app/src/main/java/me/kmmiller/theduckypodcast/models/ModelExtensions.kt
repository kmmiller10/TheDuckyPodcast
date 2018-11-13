package me.kmmiller.theduckypodcast.models

fun UserModel.equalTo(other: UserModel): Boolean {
    if(email == other.email && age == other.age && gender == other.gender && state == other.state) {
        return true
    }
    return false
}