package giovannicornachini.simulando

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.content.Intent
import android.opengl.Visibility
import android.util.Log
import giovannicornachini.simulando.Models.Question
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.support.design.widget.Snackbar




class MainActivity : AppCompatActivity() {
    val QUESTION_LIST = QuestionActivity.QUESTION_LIST

    val simulandoAPI = SimulandoAPIHelper.api
    var startBtn:Button? = null
    var questions: ArrayList<Question> = ArrayList<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBtn = findViewById(R.id.startBtn) as Button
        startBtn!!.setOnClickListener(View.OnClickListener { view ->
            initQuestions()
        })

        getAllQuestions()
    }

    fun enableInit(){
        startBtn!!.isEnabled = true
        startBtn!!.visibility = android.transition.Visibility.MODE_IN
    }

    fun initQuestions(){
        """ Init QuestionActivity with questions. """
        Log.d("Init Question Activity", questions.toString())

        val intent = Intent(this, QuestionActivity::class.java)
        intent.putExtra(QUESTION_LIST, questions)

        startActivity(intent)
    }

    // REST Call
    fun getAllQuestions(){
        // Create a call instance for looking up Retrofit contributors.
        val call = simulandoAPI.questions()

        call.enqueue(object : Callback<List<Question>> {
            override fun onFailure(call: Call<List<Question>>?, t: Throwable?) {
                if (t != null) {
                    Log.d("Error", t.message)
                    Snackbar.make(findViewById(R.id.startBtn), t.message!!,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show()
                }
            }

            override fun onResponse(call: Call<List<Question>>?, response: Response<List<Question>>?) {
                val response_questions = response?.body()
                for(question in response_questions!!){
                    questions.add(question)
                }
                enableInit()
            }
        })
    }
}
