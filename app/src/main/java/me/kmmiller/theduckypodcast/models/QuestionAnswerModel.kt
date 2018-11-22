package me.kmmiller.theduckypodcast.models

import com.google.firebase.firestore.DocumentSnapshot
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class QuestionAnswerModel : RealmObject() {
    var type = 0
    var question = ""
    var answers = RealmList<String>()
    var hasOtherField = false

    fun setAnswerType(type: AnswerType) {
        this.type = AnswerType.getValue(type)
    }

    fun getAnswerType(): AnswerType {
       return AnswerType.getType(type)
    }
}