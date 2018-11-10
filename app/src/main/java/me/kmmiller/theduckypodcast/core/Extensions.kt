package me.kmmiller.theduckypodcast.core

import io.realm.Realm
import me.kmmiller.theduckypodcast.base.BaseModel


fun Realm.findById(id: String): BaseModel? {
    return where(BaseModel::class.java).equalTo("id", id).findFirst()
}
