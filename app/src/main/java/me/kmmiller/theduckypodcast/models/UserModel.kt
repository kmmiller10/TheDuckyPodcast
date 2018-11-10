package me.kmmiller.theduckypodcast.models

import com.google.firebase.firestore.DocumentSnapshot
import io.realm.annotations.RealmClass
import me.kmmiller.theduckypodcast.base.BaseModel

@RealmClass
class UserModel : BaseModel() {

    var age = 0
    var gender = ""
    var state = ""

    override fun toRealmModel(document: DocumentSnapshot) {
        id = document.id
        age = document["age"] as? Int ?: 0
        gender = document["gender"] as? String ?: ""
        state = document["state"] as? String ?: ""
    }

    override fun fromRealmModel(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["age"] = age
        map["gender"] = gender
        map["state"] = state
        return map
    }
}