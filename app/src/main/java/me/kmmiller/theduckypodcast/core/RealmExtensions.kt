package me.kmmiller.theduckypodcast.core

import io.realm.Realm
import me.kmmiller.theduckypodcast.models.DailiesModel
import me.kmmiller.theduckypodcast.models.SeriesModel
import me.kmmiller.theduckypodcast.models.UserModel


fun Realm.findUserById(id: String?): UserModel? {
    if(id == null) return null
    return where(UserModel::class.java).equalTo("id", id).findFirst()
}

fun Realm.findSeriesModel(id: String?): SeriesModel? {
    if(id == null) return null
    return where(SeriesModel::class.java).equalTo("id", id).findFirst()
}

fun Realm.findDailiesModel(id: String?): DailiesModel? {
    if(id == null) return null
    return where(DailiesModel::class.java).equalTo("id", id).findFirst()
}