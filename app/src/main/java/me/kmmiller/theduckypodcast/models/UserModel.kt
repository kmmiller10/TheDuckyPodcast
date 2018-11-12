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

    var email = ""
    var age = 0L
    var gender = ""
    var state = ""

    fun toRealmModel(document: DocumentSnapshot) {
        id = document.id
        email = document["email"] as? String ?: ""
        age = document["age"] as? Long ?: 0L
        gender = document["gender"] as? String ?: ""
        state = document["state"] as? String ?: ""
    }

    fun fromRealmModel(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["email"] = email
        map["age"] = age
        map["gender"] = gender
        map["state"] = state
        return map
    }

    companion object {
        @JvmStatic
        fun getPositionOfGender(key: String): Int {
            return when(key.toLowerCase()) {
                "male" -> 1
                "female" -> 2
                "other" -> 3
                else -> 0
            }
        }
    }
}