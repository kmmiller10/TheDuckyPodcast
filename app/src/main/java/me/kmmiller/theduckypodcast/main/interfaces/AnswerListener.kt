package me.kmmiller.theduckypodcast.main.interfaces

interface AnswerListener{
    fun setAnswer(position: Int, input: String?)
    fun getAnswer(): Int
    fun getOtherInput(): String?
}