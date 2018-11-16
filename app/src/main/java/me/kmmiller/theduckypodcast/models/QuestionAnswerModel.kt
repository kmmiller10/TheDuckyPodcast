package me.kmmiller.theduckypodcast.models

import com.google.firebase.firestore.DocumentSnapshot
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required

@RealmClass
open class QuestionAnswerModel : RealmObject(), RModel {
    var type = 0
    var question = ""
    var answers = RealmList<String>()
    var hasOtherField = false

    var selectedAnswerIndex = 0L
    var otherInput = ""

    fun setAnswerType(type: AnswerType) {
        this.type = AnswerType.getValue(type)
    }

    fun getAnswerType(): AnswerType {
       return AnswerType.getType(type)
    }

    override fun toRealmModel(document: DocumentSnapshot) {
        // This model doesn't derive from a document directly, but instead must be constructed when grabbing the
        // "dailies" document and parsing out the questions and answers
    }

    override fun fromRealmModel(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["selectedAnswer"] = selectedAnswerIndex
        if(hasOtherField && selectedAnswerIndex == (answers.size - 1).toLong()) {
            map["other"] = otherInput
        }
        return map
    }
}