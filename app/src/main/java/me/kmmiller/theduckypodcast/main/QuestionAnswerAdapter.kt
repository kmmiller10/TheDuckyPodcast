package me.kmmiller.theduckypodcast.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import me.kmmiller.theduckypodcast.databinding.QuestionAnswerBinding
import me.kmmiller.theduckypodcast.models.QuestionAnswerModel
import me.kmmiller.theduckypodcast.models.AnswerType

class QuestionAnswerAdapter(private val items: ArrayList<QuestionAnswerModel>) : RecyclerView.Adapter<QuestionAnswerAdapter.QuestionAnswerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionAnswerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return QuestionAnswerViewHolder(QuestionAnswerBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: QuestionAnswerViewHolder, position: Int) {
        val item = items[position]

        if(position == items.size - 1) {
            holder.hideDiv()
        }

        holder.setOtherField(item.hasOtherField)

        holder.addQuestion(item.question)

        if(item.getAnswerType() == AnswerType.RADIO_BUTTON) {
            holder.addRadioAnswers(ArrayList(item.answers))
        }
    }

    override fun getItemCount(): Int = items.size

    inner class QuestionAnswerViewHolder(private val binding: QuestionAnswerBinding) : RecyclerView.ViewHolder(binding.root) {
        private val context = itemView.context
        private var hasOtherField = false

        fun hideDiv() {
            binding.div.visibility = View.GONE
        }

        fun setOtherField(hasOtherField: Boolean){
            this.hasOtherField = hasOtherField
        }

        fun addQuestion(question: String) {
            binding.question.text = question
        }

        fun addRadioAnswers(answers: ArrayList<String>) {
            answers.forEach {answer ->
                val radioButton = AppCompatRadioButton(context)
                radioButton.id = answers.indexOf(answer)
                radioButton.text = answer

                radioButton.setOnClickListener {
                    binding.otherAnswer.visibility = if(answers.last() == answer && hasOtherField) View.VISIBLE else View.GONE
                }

                binding.radioAnswers.addView(radioButton)
            }
        }

        fun getAnswer() {
            binding.radioAnswers.checkedRadioButtonId
        }
    }
}