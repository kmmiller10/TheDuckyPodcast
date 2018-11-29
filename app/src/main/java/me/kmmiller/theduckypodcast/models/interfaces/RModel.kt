package me.kmmiller.theduckypodcast.models.interfaces

import com.google.firebase.firestore.DocumentSnapshot

interface RModel {
    fun toRealmModel(document: DocumentSnapshot)
    fun fromRealmModel(): HashMap<String, Any>
}