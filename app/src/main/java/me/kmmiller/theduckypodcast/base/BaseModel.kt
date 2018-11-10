package me.kmmiller.theduckypodcast.base

import com.google.firebase.firestore.DocumentSnapshot
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
abstract class BaseModel : RealmObject(), RealmModel {
    @Required
    @PrimaryKey
    var id: String = ""

    abstract fun toRealmModel(document: DocumentSnapshot)
    abstract fun fromRealmModel(): HashMap<String, Any>
}