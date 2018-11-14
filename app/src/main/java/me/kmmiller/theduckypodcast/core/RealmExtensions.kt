package me.kmmiller.theduckypodcast.core

import io.realm.Realm
import me.kmmiller.theduckypodcast.models.SeriesModel
import me.kmmiller.theduckypodcast.models.UserModel


fun Realm.findUserById(id: String): UserModel? {
    return where(UserModel::class.java).equalTo("id", id).findFirst()
}

fun Realm.findSeriesModel(id: String): SeriesModel? {
    return where(SeriesModel::class.java).equalTo("id", id).findFirst()
}