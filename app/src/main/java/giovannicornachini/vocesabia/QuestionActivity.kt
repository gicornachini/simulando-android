package giovannicornachini.vocesabia

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import giovannicornachini.vocesabia.Models.Question
import android.widget.AdapterView.OnItemClickListener
import giovannicornachini.vocesabia.Models.QuestionAlternative
import android.widget.Toast
import android.widget.EditText




class QuestionActivity : AppCompatActivity() {
    val simulandoAPI = SimulandoAPIHelper.api
    var questions: ArrayList<Question>? = null
    private val context = this

    var actualIndex: Int = 0

    companion object {
        val QUESTION_LIST = "giovannicornachini.simulando.QUESTIONS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        // Toolbar
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = "Pergunta"
        setSupportActionBar(toolbar)

        // Get extra params
        getQuestions()

        // Load first question
        if (questions != null && questions!!.isNotEmpty()) {
            loadQuestion(questions!!.first())
        }

    }

    fun getQuestions() {
        val extras = intent.extras
        questions = extras.getSerializable(QUESTION_LIST) as ArrayList<Question>?
    }

    private fun loadQuestion(question: Question) {
        var index = questions!!.indexOf(question)
        index += 1

        val lv = findViewById(R.id.list) as ListView
        lv.adapter = ListQuestionAdapter(this, question, questions!!)
        lv.onItemClickListener = OnItemClickListener { parent, view, position, id ->

            // If not an Alternative cell, return
            var cell = parent.getItemAtPosition(position)
            if (cell !is QuestionAlternative) {
                return@OnItemClickListener
            }

            var alternative = cell
            if (alternative.isCorrect(question)) {
                Log.d("QuestionActivity", "Correct Choice")
                view.setBackgroundResource(R.color.correctAlternative)
            } else {
                Log.d("QuestionActivity", "Wrong Choice")
                view.setBackgroundResource(R.color.wrongAlternative)
            }

            loadNextQuestion()

        }
    }

    private fun loadNextQuestion() {

        if (this.hasNextQuestion()) {
            actualIndex += 1
            loadQuestion(questions!![actualIndex])
        }
    }

    private fun hasNextQuestion(): Boolean {
        var nextIndex = actualIndex + 1
        if (questions!!.size > nextIndex) {
            return true
        }
        return false
    }

    fun answeredDialog(correct: Boolean){
        val dBuilder = AlertDialog.Builder(context)
        val mView = layoutInflater.inflate(R.layout.dialog_answer, null)
        val confirmBtn = mView.findViewById(R.id.confirmBtn) as Button
        val cancelBtn = mView.findViewById(R.id.cancelBtn) as Button
        val resultTxt = mView.findViewById(R.id.resultTxt) as TextView
        val tryAgainTxt = mView.findViewById(R.id.tryAgainTxt) as TextView

        val alert = dBuilder.create()
        alert.setCancelable(false)
        alert.setCanceledOnTouchOutside(false)

        alert.setView(mView)
        if (correct){
            resultTxt.text = "Resposta correta :)"
            cancelBtn.visibility = View.GONE
            tryAgainTxt.visibility = View.GONE

            if (hasNextQuestion()) {
                confirmBtn.text = "Próxima pergunta"

                confirmBtn.setOnClickListener(View.OnClickListener {
                    loadNextQuestion()
                    alert.dismiss()
                })
            } else {
                confirmBtn.text = "Voltar ao menu"

                confirmBtn.setOnClickListener({
                    alert.dismiss()
                    context.finish()
                })
            }
        } else {
            resultTxt.text = "Resposta incorreta :("
            confirmBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)

            if (hasNextQuestion()) {
                confirmBtn.text = "Tentar novamente"
                cancelBtn.text = "Próxima pergunta"

                cancelBtn.setOnClickListener(View.OnClickListener {
                    loadNextQuestion()
                    alert.dismiss()
                })

                confirmBtn.setOnClickListener({
                    alert.dismiss()
                })

            } else {
                confirmBtn.text = "Tentar novamente"
                cancelBtn.text = "Voltar ao menu"

                cancelBtn.setOnClickListener(View.OnClickListener {
                    alert.dismiss()
                    context.finish()
                })

                confirmBtn.setOnClickListener({
                    alert.dismiss()
                })
            }

        }

        alert.show()
    }


    // List
    private class ListQuestionAdapter(context: Context,
                                      internal var question: Question,
                                      internal var questions: ArrayList<Question>) : BaseAdapter() {

        private val mInflator: LayoutInflater = LayoutInflater.from(context)
        private val mContext: Context = context

        override fun getCount(): Int {
            return 2 + question.alternatives.count()
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return when (position) {
                0 -> 0
                1 -> question
                else -> question.alternatives[position-2]
            }
        }

        override fun isEnabled(position: Int): Boolean {
            if (position in 2..6) {
                return true
            }

            return false
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?

            when (position) {
                0 -> {
                    val questionProgress = (questions.indexOf(question)+1).toFloat()
                    val progress = (questionProgress/questions.count()) * 100
                    view = this.mInflator.inflate(R.layout.question_header, parent, false)
                    val cell = ListQuestionHeader(view, progress.toInt())
                    view.tag = cell
                    return view
                }
                1 -> {
                    view = this.mInflator.inflate(R.layout.question_row, parent, false)
                    val cell = ListTextHolder(view, question)
                    view.tag = cell

                    return view
                }
                in 2..6 -> {
                    val alternative = this.getItem(position) as QuestionAlternative

                    view = this.mInflator.inflate(R.layout.alternative_row, parent, false)
                    val cell = ListQuestionAlternative(view, alternative, question, mContext)
                    view.tag = cell

                    return view
                }
            }

            view = this.mInflator.inflate(R.layout.question_row, parent, false)
            return view
        }
    }

    // Cells
    private class ListTextHolder(row: View?, question: Question) {
        val label: TextView
        val question: Question

        init {
            this.label = row?.findViewById(R.id.label) as TextView
            this.question = question
            this.setLabelText()
        }

        fun setLabelText() {
            this.label.text = this.question.text
        }
    }

    private class ListQuestionAlternative(row: View?,
                                          alternative: QuestionAlternative,
                                          question: Question,
                                          context: Context) {
        val alternativeBtn: Button
        val alternative: QuestionAlternative
        val question: Question
        private val mContext: Context = context

        init {
            this.alternativeBtn = row?.findViewById(R.id.alternativeBtn) as Button
            this.alternative = alternative
            this.question = question

            this.setAlternativeText()
            this.setOnClick()
        }

        fun setAlternativeText(){
            val alternativeText = this.alternative.text.capitalize()
            val alternativeLetter = this.alternative.letter.capitalize()
            this.alternativeBtn.text = "$alternativeLetter) $alternativeText"
        }

        fun setOnClick() {
            this.alternativeBtn.setOnClickListener(object: View.OnClickListener {
                override fun onClick(view: View): Unit {
                    var context = mContext as QuestionActivity

                    if (alternative.isCorrect(question)) {
                        Log.d("ListQuestionAlternative", "Correct Choice")
                        view.setBackgroundResource(R.color.correctAlternative)
                    } else {
                        Log.d("ListQuestionAlternative", "Wrong Choice")
                        view.setBackgroundResource(R.color.wrongAlternative)
                    }

                    context.answeredDialog(alternative.isCorrect(question))

                    alternativeBtn.setTextColor(Color.WHITE)
                }
            })
        }
    }

    private class ListQuestionHeader(row: View?, progress: Int){
        val progressbar: ProgressBar
        val progress: Int

        init {
            this.progressbar = row?.findViewById(R.id.progressBar) as ProgressBar
            this.progressbar.progressDrawable.setColorFilter(
                    Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN)
            this.progress = progress
            setProgressBar()
        }

        fun setProgressBar() {
            this.progressbar.setProgress(progress)
        }
    }

}

