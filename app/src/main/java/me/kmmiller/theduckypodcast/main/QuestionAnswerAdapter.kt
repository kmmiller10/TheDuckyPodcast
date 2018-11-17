package me.kmmiller.theduckypodcast.main

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import me.kmmiller.theduckypodcast.databinding.QuestionAnswerBinding
import me.kmmiller.theduckypodcast.models.QuestionAnswerModel
import me.kmmiller.theduckypodcast.models.AnswerType
import me.kmmiller.theduckypodcast.utils.nonNullString
import me.kmmiller.theduckypodcast.utils.onTextChangedListener

class QuestionAnswerAdapter(private val items: ArrayList<QuestionAnswerModel>) : RecyclerView.Adapter<QuestionAnswerAdapter.QuestionAnswerViewHolder>() {
    private val listeners = ArrayList<AnswerListener>()

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
        listeners.add(holder as AnswerListener)
    }

    override fun getItemCount(): Int = items.size

    /**
     * Returns SparseArray with index of the question, position of answer for the question,
     * and additional input if it was an other option
     */
    fun getAnswers(): SparseArray<Pair<Int, String?>> {
        val answers = SparseArray<Pair<Int, String?>>()
        var index = 0
        listeners.forEach {
            answers.append(index, Pair(it.getAnswer(), it.getOtherInput()))
            index++
        }
        return answers
    }

    inner class QuestionAnswerViewHolder(private val binding: QuestionAnswerBinding) : RecyclerView.ViewHolder(binding.root), AnswerListener {
        private val context = itemView.context
        private var hasOtherField = false
        private var count = 0

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
            count = answers.size
            answers.forEach {answer ->
                val radioButton = AppCompatRadioButton(context)
                radioButton.id = answers.indexOf(answer)
                radioButton.text = answer

                radioButton.setOnClickListener {
                    binding.otherAnswer.visibility = if(answers.last() == answer && hasOtherField) View.VISIBLE else View.GONE
                    clearOtherAnswerError()
                }

                binding.otherAnswer.onTextChangedListener {
                    clearOtherAnswerError()
                }

                binding.radioAnswers.addView(radioButton)
            }
        }

        override fun getAnswer(): Int {
            return binding.radioAnswers.checkedRadioButtonId // Returns -1 if none selected
        }

        override fun getOtherInput(): String? {
            // Get last item
            if(getAnswer() == (count - 1) && hasOtherField) {
                val otherInput = binding.otherAnswer.text?.toString().nonNullString()
                // Show required error if empty
                binding.otherAnswerError.visibility = if(otherInput.isEmpty()) View.VISIBLE else View.GONE
                return otherInput
            }
            return null
        }

        private fun clearOtherAnswerError() {
            binding.otherAnswerError.visibility = View.GONE
        }
    }

    interface AnswerListener {
        fun getAnswer(): Int
        fun getOtherInput(): String?
    }
}