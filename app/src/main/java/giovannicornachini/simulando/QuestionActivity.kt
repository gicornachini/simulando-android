package giovannicornachini.simulando

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import giovannicornachini.simulando.Models.Question
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class QuestionActivity : AppCompatActivity() {
    val simulandoAPI = SimulandoAPIHelper.api
    var questions: ArrayList<Question>? = null

    var questionIndexTxt: TextView? = null
    var questionTxt: TextView? = null
    var alternativeABtn: Button? = null

    var actualIndex: Int = 0

    companion object {
        val QUESTION_LIST = "giovannicornachini.simulando.QUESTIONS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        // Toolbar
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = "Exercício"
        setSupportActionBar(toolbar)

        // Get extra params
        getQuestions()

        // Initialize view objects
        questionIndexTxt = findViewById(R.id.questionIndexIndicatorTxt) as TextView
        questionTxt = findViewById(R.id.questionTxt) as TextView
        alternativeABtn = findViewById(R.id.alternativeABtn) as Button

        // Load first question
        if (questions != null && questions!!.isNotEmpty()){
            loadQuestion(questions!!.first())
        }

        alternativeABtn!!.setOnClickListener(View.OnClickListener { view ->
            loadNextQuestion()
        })

    }

    fun getQuestions(){
        val extras = intent.extras
        questions = extras.getSerializable(QUESTION_LIST) as ArrayList<Question>?
    }

    fun setIndicator(index:Int, count: Int){
        questionIndexTxt?.text = "$index/$count"
        actualIndex = index
    }

    private fun loadQuestion(question: Question){
        questionTxt?.text = question.textHtmlToView
        var index = questions!!.indexOf(question)
        index += 1
        setIndicator(index, questions!!.size)
    }

    private fun loadNextQuestion(){
        if (questions!!.size > actualIndex) {
            loadQuestion(questions!![actualIndex])
        }
    }

}
