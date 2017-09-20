package giovannicornachini.simulando

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.content.Intent



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById(R.id.startBtn) as Button
        fab.setOnClickListener(View.OnClickListener { view ->
            initQuestion()
        })
    }


    fun initQuestion(){
        """ Init QuestionActivity with questions. """
        val intent = Intent(this, QuestionActivity::class.java)
        // TODO: send questions
        startActivity(intent)
    }
}
