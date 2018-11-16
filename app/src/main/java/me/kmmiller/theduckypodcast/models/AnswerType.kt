package me.kmmiller.theduckypodcast.models

enum class AnswerType (val type: Int) {
    RADIO_BUTTON(0),
    CHECK_BOX(1),
    EDIT_TEXT(2),
    NONE(3);

    companion object {
        fun fromFirebaseType(type: String): AnswerType {
            return when(type) {
                "_radio" -> RADIO_BUTTON
                "_check" -> CHECK_BOX
                "_input" -> EDIT_TEXT
                else -> NONE
            }
        }

        fun getType(value: Int): AnswerType = when (value) {
            0 -> RADIO_BUTTON
            1 -> CHECK_BOX
            2 -> EDIT_TEXT
            else -> NONE
        }

        fun getValue(type: AnswerType): Int = when (type) {
            RADIO_BUTTON -> 0
            CHECK_BOX -> 1
            EDIT_TEXT -> 2
            NONE -> 3
        }
    }
}