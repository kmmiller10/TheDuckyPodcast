package me.kmmiller.theduckypodcast.models

import android.util.SparseArray
import com.google.firebase.firestore.DocumentSnapshot
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import me.kmmiller.theduckypodcast.utils.getHashMap
import me.kmmiller.theduckypodcast.utils.nonNullString

@RealmClass
open class DailiesModel : RealmObject(), RModel {
    @Required
    @PrimaryKey
    var id: String = ""

    var title = ""
    var items = RealmList<QuestionAnswerModel>()
    var isSubmitted = false

    override fun toRealmModel(document: DocumentSnapshot) {
        id = document.id
        title = document.get("title").nonNullString()

        val questionAnswers = document.getHashMap("questionAnswers").toSortedMap()

        for(entry in questionAnswers.entries) {
            val model = QuestionAnswerModel()

            model.question = entry.key

            val answers = entry.value

            // The first value is always the answer type
            val firebaseAnswerType = answers.firstOrNull() ?: continue
            when(AnswerType.fromFirebaseType(firebaseAnswerType)) {
                AnswerType.RADIO_BUTTON -> {
                    model.setAnswerType(AnswerType.RADIO_BUTTON)
                }
                AnswerType.CHECK_BOX -> {
                    // TODO Not implemented yet
                }
                AnswerType.EDIT_TEXT -> {
                    // TODO Not implemented yet
                }
                AnswerType.NONE -> {
                    // Should never reach here
                }
            }
            answers.removeAt(0)

            // The last value will be _input if there is an other field
            val otherField = answers.lastOrNull() ?: continue
            if(AnswerType.fromFirebaseType(otherField) == AnswerType.EDIT_TEXT) {
                model.hasOtherField = true
                answers.removeAt(answers.size - 1) // Remove the _input field since it is no longer needed
            }

            // Add the answers (omitting the first and last field (if last field was _input)
            model.answers.addAll(answers)

            items.add(model)
        }
    }

    override fun fromRealmModel(): HashMap<String, Any> {
        return HashMap()
    }

    companion object {
        @JvmStatic
        fun createSubmittableModel(dailyId: String, answers: SparseArray<Pair<Int, String?>>, additionalComments: String): HashMap<String, Any> {
            val map = HashMap<String, Any>()
            map["dailyId"] = dailyId
            map["additionalComments"] = additionalComments

            val answerPositions = ArrayList<Int>()
            val inputs = ArrayList<String>()
            for(i in 0 until answers.size()) {
                val answer = answers[i]
                val position = answer.first
                val input = answer.second.nonNullString()

                answerPositions.add(position)
                inputs.add(input)
            }

            map["answers"] = answerPositions
            map["inputs"] = inputs
            return map
        }
    }
}