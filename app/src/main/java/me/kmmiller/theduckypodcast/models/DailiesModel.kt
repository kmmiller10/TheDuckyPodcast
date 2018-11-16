package me.kmmiller.theduckypodcast.models

import android.util.SparseArray
import com.google.firebase.firestore.DocumentSnapshot
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import me.kmmiller.theduckypodcast.utils.getStringArrayList
import me.kmmiller.theduckypodcast.utils.nonNullString

@RealmClass
open class DailiesModel : RealmObject(), RModel {
    @Required
    @PrimaryKey
    var id: String = ""

    var title = ""
    var items = RealmList<QuestionAnswerModel>()

    override fun toRealmModel(document: DocumentSnapshot) {
        id = document.id
        title = document.get("title").nonNullString()

        val questions = document.getStringArrayList("questions")

        // Collect answers - each answer array has a key equal to its question's position
        val answers = SparseArray<ArrayList<String>>()
        for((index, _) in questions.withIndex()) {
            val answerList = document.getStringArrayList(index.toString())
            answers.append(index, answerList)
        }

        for(i in 0 until questions.size) {
            val model = QuestionAnswerModel()

            model.question = questions[i]

            // The first value is always the answer type
            val firebaseAnswerType = answers[i].firstOrNull() ?: continue
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
            answers[i].removeAt(0) // Remove the answer type value since it is no longer needed

            // The last value will be _input if there is an other field
            val otherField = answers[i].lastOrNull() ?: continue
            if(AnswerType.fromFirebaseType(otherField) == AnswerType.EDIT_TEXT) {
                model.hasOtherField = true
                answers[i].removeAt(answers[i].size - 1) // Remove the _input field since it is no longer needed
            }

            // Add the answers (omitting the first and last field (if last field was _input)
            model.answers.addAll(answers[i])

            items.add(model)
        }
    }

    override fun fromRealmModel(): HashMap<String, Any> {
        return HashMap()
    }
}