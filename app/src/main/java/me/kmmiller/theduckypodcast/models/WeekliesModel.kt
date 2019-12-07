package me.kmmiller.theduckypodcast.models

import android.util.SparseArray
import com.google.firebase.firestore.DocumentSnapshot
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import me.kmmiller.theduckypodcast.models.interfaces.RModel
import me.kmmiller.theduckypodcast.utils.getHashMap
import me.kmmiller.theduckypodcast.utils.nonNullString

@RealmClass
open class WeekliesModel: RealmObject(), RModel {
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
            model.setAnswerType(AnswerType.fromFirebaseType(firebaseAnswerType))
            answers.removeAt(0)

            // The last value will be _input if there is an other field
            val otherField = answers.lastOrNull() ?: continue
            if(AnswerType.fromFirebaseType(otherField) == AnswerType.EDIT_TEXT) {
                model.hasOtherField = true
                answers.removeAt(answers.size - 1) // Remove the _input field since it is no longer needed
            }

            // Add the answers (omitting the first and last field if last field was _input)
            model.answers.addAll(answers)

            items.add(model)
        }
    }

    override fun fromRealmModel(): HashMap<String, Any> = HashMap()

    companion object {
        @JvmStatic
        fun createSubmittableModel(userId: String, weeklyId: String, answers: SparseArray<ParcelableAnswer>, additionalComments: String): HashMap<String, Any> {
            val map = HashMap<String, Any>()
            map["additionalComments"] = additionalComments
            map["userId"] = userId
            map["weeklyId"] = weeklyId

            val answersMap = HashMap<String, ArrayList<Any>>()
            for(i in 0 until answers.size()) {
                val answer = answers[i]

                val answerList = ArrayList<Any>()
                // Add 1 to the answer position since the first value (at index 0) in the weeklies questionAnswer field is the input type
                answerList.add(if(answer.otherInput.nonNullString().isEmpty()) answer.answerPosition+1 else answer.otherInput.nonNullString())

                answersMap[i.toString()] = answerList
            }

            map["answers"] = answersMap
            return map
        }
    }
}