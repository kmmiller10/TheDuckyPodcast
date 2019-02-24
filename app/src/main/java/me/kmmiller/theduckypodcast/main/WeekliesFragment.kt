package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.WeekliesFragmentBinding
import me.kmmiller.theduckypodcast.main.interfaces.IRestoreState
import me.kmmiller.theduckypodcast.main.interfaces.NavItem
import me.kmmiller.theduckypodcast.main.interfaces.SavableFragment
import me.kmmiller.theduckypodcast.models.ParcelableAnswer
import me.kmmiller.theduckypodcast.models.WeekliesModel
import me.kmmiller.theduckypodcast.models.findWeekliesModel
import me.kmmiller.theduckypodcast.utils.nonNullString

class WeekliesFragment: BaseFragment(), NavItem, SavableFragment, IRestoreState {
    private lateinit var binding: WeekliesFragmentBinding

    private var menu: Menu? = null
    private var adapter: QuestionAnswerAdapter? = null
    private var restoredAnswers: SparseArray<ParcelableAnswer>? = null

    override fun getTitle(): String = getString(R.string.weeklies)
    override fun getNavId(): Int = R.id.nav_weeklies

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = WeekliesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        viewModel?.let {
            if(it.weeklyId.isNotEmpty()) {
                load(it.weeklyId)
            }
        }

        if(viewModel?.weeklyId.nonNullString().isEmpty()) {
            showCancelableProgress(getString(R.string.loading))
        }

        getWeeklyId(fb) {
            if(context != null) {
                if(it != viewModel?.weeklyId.nonNullString()) {
                    // This is a new weekly or it hasn't been loaded into the view model yet
                    viewModel?.weeklyId = it

                    showCancelableProgress(getString(R.string.loading))
                    fb.collection("weeklies")
                        .document(it)
                        .get()
                        .addOnSuccessListener { doc ->
                            val model = WeekliesModel()
                            model.toRealmModel(doc)
                            realm?.executeTransaction { rm ->
                                rm.copyToRealmOrUpdate(model)
                            }
                            load(it)
                        }
                        .addOnFailureListener { e ->
                            handleError(e)
                            dismissProgress()
                        }
                } else {
                    // Already have this weekly, just check for status
                    dismissProgress()
                    checkAnsweredStatus()
                }
            }
        }

        savedInstanceState?.let {
            restoredAnswers = it.getSparseParcelableArray(CURRENT_ANSWERS)
            binding.additionalComments.setText(it.getString(ADDITIONAL_COMMENTS, ""))
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        val currentAnswers = adapter?.getAnswers()
        if(currentAnswers != null) outState.putSparseParcelableArray(CURRENT_ANSWERS, currentAnswers)

        outState.putString(ADDITIONAL_COMMENTS, binding.additionalComments.text?.nonNullString())

        super.onSaveInstanceState(outState)
    }

    override fun onInstanceRestored(position: Int) {
        restoredAnswers?.let {
            adapter?.let { adapter ->
                adapter.setAnswers(it)
                if(position == adapter.itemCount ) restoredAnswers = null
            }
        }
    }

    private fun load(id: String) {
        try {
            realm?.findWeekliesModel(id)?.let {
                binding.title.text = it.title

                adapter = QuestionAnswerAdapter(ArrayList(it.items), this)
                binding.questionAnswerList.adapter = adapter
                binding.questionAnswerList.layoutManager = LinearLayoutManager(requireContext())
                binding.questionAnswerList.isNestedScrollingEnabled = false

                checkAnsweredStatus()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Context probably null")
            dismissProgress()
        }
    }

    private fun checkAnsweredStatus() {
        val weeklyId = viewModel?.weeklyId
        if(weeklyId != null) {
            auth?.currentUser?.uid?.let {
                fb.collection("weeklies-responses/$it/$weeklyId")
                    .document(weeklyId)
                    .get()
                    .addOnSuccessListener {
                        // User has already submitted this weekly, show results
                        dismissProgress()

                        val weeklyModel = realm?.findWeekliesModel(viewModel?.weeklyId)
                        if (weeklyModel != null) {
                            realm?.executeTransaction {
                                weeklyModel.isSubmitted = true
                            }
                        }

                        pushFragment(WeekliesResultsFragment(), true, false, TAG)
                    }
                    .addOnFailureListener { e ->
                        dismissProgress()
                        if ((e as FirebaseFirestoreException).code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            // User has not completed this weekly yet
                        } else {
                            // Ran into some other error - perhaps no internet connection? Handle error like normal
                            handleError(e)
                        }
                    }
            }
        }
    }

    private fun getWeeklyId(fb: FirebaseFirestore, onComplete: (String) -> Unit) {
        fb.collection("weeklies")
            .document("get-weekly-id")
            .get()
            .addOnSuccessListener {
                val id = it.get("id").nonNullString()
                onComplete.invoke(id)
            }
            .addOnFailureListener {
                dismissProgress()
                handleError(it)
            }
    }

    private fun validateAnswers(answers: SparseArray<ParcelableAnswer>): Boolean {
        for(i in 0 until answers.size()) {
            val answer = answers.get(i)

            val answerPosition = answer.answerPosition
            val otherInput = answer.otherInput

            if(answerPosition == -1) {
                // -1 indicates that no answer was given for that question
                binding.scrollView.scrollTo(0, 0) // Scroll to top so error is visible
                binding.answerError.visibility = View.VISIBLE
                return false
            } else if(otherInput != null && otherInput.isEmpty()) {
                // Empty answer for "other" selection
                return false
            }
        }
        return true
    }

    override fun onSave() {
        val answers = adapter?.getAnswers() ?: return
        val additionalComments = binding.additionalComments.text?.toString().nonNullString()

        fun save(weeklyId: String) {
            showProgress(getString(R.string.saving))
            if(validateAnswers(answers)) {
                auth?.uid?.let { userId ->
                    val submittableModel = WeekliesModel.createSubmittableModel(userId, weeklyId, answers, additionalComments)
                    // Send the data to server after it has been validated, disable save and show results
                    fb.collection("weeklies-responses")
                        .document(userId)
                        .collection(weeklyId)
                        .document(weeklyId)
                        .set(submittableModel)
                        .addOnSuccessListener {
                            val weekliesModel = realm?.findWeekliesModel(viewModel?.weeklyId)
                            if (weekliesModel != null) {
                                realm?.executeTransaction {
                                    weekliesModel.isSubmitted = true
                                }
                            }
                            dismissProgress()
                            checkAnsweredStatus()
                        }
                        .addOnFailureListener { e ->
                            handleError(e)
                            dismissProgress()
                        }
                }
            } else {
                dismissProgress()
            }
        }

        val weeklyId = viewModel?.weeklyId
        if(weeklyId == null) {
            getWeeklyId(fb) {
                save(it)
            }
        } else {
            save(weeklyId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.savable_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.save).title = getString(R.string.submit).toUpperCase()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item != null && item.itemId == R.id.save) {
            onSave()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val TAG = "weeklies_fragment"
        const val CURRENT_ANSWERS = "current_answers"
        const val ADDITIONAL_COMMENTS = "additional_comments"
    }
}