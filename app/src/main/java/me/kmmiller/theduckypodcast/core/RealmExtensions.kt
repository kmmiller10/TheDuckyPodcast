package me.kmmiller.theduckypodcast.core

import io.realm.Realm
import me.kmmiller.theduckypodcast.models.UserModel


fun Realm.findById(id: String): UserModel? {
    return where(UserModel::class.java).equalTo("id", id).findFirst()
}