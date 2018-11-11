package me.kmmiller.theduckypodcast.models

import com.google.firebase.firestore.DocumentSnapshot
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
open class UserModel : RealmObject() {
    @Required
    @PrimaryKey
    var id: String = ""

    var age = 0
    var gender = ""
    var state = ""

    fun toRealmModel(document: DocumentSnapshot) {
        id = document.id
        age = document["age"] as? Int ?: 0
        gender = document["gender"] as? String ?: ""
        state = document["state"] as? String ?: ""
    }

    fun fromRealmModel(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["age"] = age
        map["gender"] = gender
        map["state"] = state
        return map
    }
}