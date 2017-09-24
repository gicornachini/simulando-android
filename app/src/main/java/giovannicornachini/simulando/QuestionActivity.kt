package giovannicornachini.simulando

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
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

    companion object {
        val QUESTION_LIST = "giovannicornachini.simulando.QUESTIONS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val extra: Bundle = getIntent().getBundleExtra("extra")
        questions = extra.getSerializable(QUESTION_LIST) as ArrayList<Question>?
        Log.d("QuestionAct Initzd.", questions.toString())

        questionIndexTxt = findViewById(R.id.questionIndexIndicatorTxt) as TextView
        questionTxt = findViewById(R.id.questionTxt) as TextView

        if (questions != null && questions!!.isNotEmpty()){
            loadQuestion(questions!!.first())
        }

    }

    fun setIndicator(index:Int, count: Int){
        questionIndexTxt?.text = "$index/$count"
    }

    fun loadQuestion(question: Question){
        questionTxt?.setText(question.textHtmlToView)
        var index = questions!!.indexOf(question)
        index += 1
        setIndicator(index, questions!!.size)

    }

}
