package me.kmmiller.theduckypodcast.models

import com.google.firebase.firestore.DocumentSnapshot
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import me.kmmiller.theduckypodcast.models.interfaces.RModel

@RealmClass
open class ResultsAnswers : RealmObject(), RModel {
    @Required
    @PrimaryKey
    var id: String = "" // Must set using dailyId

    var additionalComments = RealmList<String>()
    var answers = RealmList<ResultsQuestionAnswerModel>()

    override fun toRealmModel(document: DocumentSnapshot) {
        try {
            if(id.isNotEmpty()) {
                val allDailyResponses = document[id] as? HashMap<String, Any>

                allDailyResponses?.let {
                    val commentsMap = it["additionalComments"] as? HashMap<String, String>
                    if(commentsMap != null) {
                        for(comment in commentsMap.entries) {
                            additionalComments.add(comment.value)
                        }
                    }

                    val unsortedAnswersMap = it["answers"] as? HashMap<String, HashMap<String, ArrayList<Any>>>
                    val unsortedKeyQuestionAnswers = it["keyQuestionAnswers"] as? HashMap<String, ArrayList<String>>

                    val answersMap = unsortedAnswersMap?.toSortedMap()
                    val keyQuestionAnswers = unsortedKeyQuestionAnswers?.toSortedMap()

                    if(answersMap != null) {
                        for(entry in answersMap.entries) {
                            val model = ResultsQuestionAnswerModel()
                            model.question = entry.key

                            if(keyQuestionAnswers != null) {
                                val answerSet = keyQuestionAnswers[model.question]
                                if(answerSet != null) {
                                    answerSet.firstOrNull()?.let { type ->
                                        model.type = AnswerType.fromFirebaseType(type).type
                                    }
                                    answerSet.removeAt(0) // First index is always the input type

                                    if(answerSet.last() == "_input") {
                                        // Remove the input field
                                        answerSet.remove(answerSet.last())
                                    }

                                    if(model.type == AnswerType.RATING.type) {
                                        // Rating only has one value specifying the maximum rating, so create array of descriptions
                                        answerSet.firstOrNull()?.let { rating ->
                                            val ratingAry = Array(rating.toInt()) { intRating -> (intRating + 1).toString() }
                                            model.answerDescriptions.addAll(ratingAry)
                                        }
                                    } else {
                                        model.answerDescriptions.addAll(answerSet)
                                    }
                                }
                            }

                            val allAnswers = entry.value
                            for(values in allAnswers.entries) {
                                for(value in values.value) {
                                    if(value is Long) {
                                        model.answers.add(value)
                                    } else {
                                        // Add as other answer
                                        model.answers.add(-1L)
                                    }
                                }
                            }

                            answers.add(model)
                        }
                    }
                }
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }

    }

    override fun fromRealmModel(): HashMap<String, Any> = HashMap()
}