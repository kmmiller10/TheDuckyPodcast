package me.kmmiller.theduckypodcast.models

import io.realm.Realm
import io.realm.RealmResults

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

fun Realm.findAllSeries(): RealmResults<SeriesModel> {
    return where(SeriesModel::class.java).findAll()
}

fun Realm.findAllDailies(): RealmResults<DailiesModel> {
    return where(DailiesModel::class.java).findAll()
}

fun Realm.findAllUsers(): RealmResults<UserModel> {
    return where(UserModel::class.java).findAll()
}