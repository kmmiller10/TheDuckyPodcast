package me.kmmiller.theduckypodcast.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class ResultsQuestionAnswerModel : RealmObject()  {
    var question = ""
    var answers = RealmList<Long>()

    fun isOtherAnswer(answer: Long): Boolean {
        return answer == -1L
    }
}