package me.kmmiller.theduckypodcast.main.surveys

import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import me.kmmiller.theduckypodcast.databinding.QuestionAnswerBinding
import me.kmmiller.theduckypodcast.main.interfaces.AnswerListener
import me.kmmiller.theduckypodcast.main.interfaces.IRestoreState
import me.kmmiller.theduckypodcast.models.AnswerType
import me.kmmiller.theduckypodcast.models.ParcelableAnswer
import me.kmmiller.theduckypodcast.models.QuestionAnswerModel
import me.kmmiller.theduckypodcast.utils.nonNullString
import me.kmmiller.theduckypodcast.utils.onTextChangedListener

class QuestionAnswerAdapter(private val items: ArrayList<QuestionAnswerModel>, private val restoreListener: IRestoreState) : RecyclerView.Adapter<QuestionAnswerAdapter.QuestionAnswerViewHolder>() {
    private val listeners = ArrayList<AnswerListener>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionAnswerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return QuestionAnswerViewHolder(QuestionAnswerBinding.inflate(inflater, parent, false), AnswerType.getType(viewType))
    }

    override fun onBindViewHolder(holder: QuestionAnswerViewHolder, position: Int) {
        val item = items[position]

        if(position == items.size - 1) {
            holder.hideDiv()
        }

        holder.setOtherField(item.hasOtherField)

        holder.addQuestion(item.question)

        when(item.getAnswerType()) {
            AnswerType.RADIO_BUTTON -> holder.addRadioAnswers(ArrayList(item.answers))
            AnswerType.RATING -> {
                val ratingCount = item.answers.first()
                ratingCount?.let {
                    holder.setRatingCount(it.toInt())
                }
            }
            else -> Log.w(TAG, "TODO")// TODO - Handle other answer types
        }

        listeners.add(holder)
        restoreListener.onInstanceRestored(position)
    }

    override fun getItemViewType(position: Int): Int = items[position].getAnswerType().type

    override fun getItemCount(): Int = items.size

    /**
     * Returns SparseArray with its index corresponding to the question, position of answer for the question,
     * and additional input if it was an other option
     */
    fun getAnswers(): SparseArray<ParcelableAnswer> {
        val answers = SparseArray<ParcelableAnswer>()
        var index = 0
        listeners.forEach {
            answers.append(index, ParcelableAnswer(it.getAnswer(), it.getOtherInput()))
            index++
        }
        return answers
    }

    fun setAnswers(answers: SparseArray<ParcelableAnswer>) {
        var index = 0
        listeners.forEach {
            val answer = answers.valueAt(index)
            it.setAnswer(answer.answerPosition, answer.otherInput)
            index++
        }
    }

    inner class QuestionAnswerViewHolder(private val binding: QuestionAnswerBinding, private val answerType: AnswerType) : RecyclerView.ViewHolder(binding.root), AnswerListener {
        private val context = itemView.context
        private var hasOtherField = false
        private var count = 0

        init {
            when(answerType) {
                AnswerType.RADIO_BUTTON -> {
                    binding.radioAnswers.visibility = View.VISIBLE
                    binding.ratingBar.visibility = View.GONE
                }
                AnswerType.RATING -> {
                    binding.ratingBar.visibility = View.VISIBLE
                    binding.radioAnswers.visibility = View.GONE
                }
                else -> {} // TODO
            }
        }

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
            if(answerType == AnswerType.RADIO_BUTTON) {
                count = answers.size
                answers.forEach { answer ->
                    val radioButton = AppCompatRadioButton(context)
                    radioButton.id = answers.indexOf(answer)
                    radioButton.text = answer

                    radioButton.setOnClickListener {
                        binding.otherAnswer.visibility =
                            if (answers.last() == answer && hasOtherField) View.VISIBLE else View.GONE
                        clearOtherAnswerError()
                    }

                    binding.otherAnswer.onTextChangedListener {
                        clearOtherAnswerError()
                    }

                    binding.radioAnswers.addView(radioButton)
                }
            } else {
                Log.e(TAG, "Tried to add radio buttons to a non-radio button question")
            }
        }

        fun setRatingCount(count: Int) {
            if(answerType == AnswerType.RATING) {
                binding.ratingBar.numStars = count
            } else {
                Log.e(TAG, "Tried to set rating count to a non-rating question")
            }
        }

        override fun setAnswer(position: Int, input: String?) {
            when(answerType) {
                AnswerType.RADIO_BUTTON -> binding.radioAnswers.check(position)
                AnswerType.RATING -> binding.ratingBar.rating = position.toFloat()
                else -> {} // TODO
            }

            input?.let {
                binding.otherAnswer.setText(it)
                binding.otherAnswer.visibility = View.VISIBLE
            }
        }

        override fun getAnswer(): Int {
            return when(answerType) {
                AnswerType.RADIO_BUTTON -> binding.radioAnswers.checkedRadioButtonId // Returns -1 if none selected
                AnswerType.RATING -> binding.ratingBar.rating.toInt()
                else -> return -1 // TODO
            }
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

    companion object {
        const val TAG = "q_and_a_adapter"
    }
}