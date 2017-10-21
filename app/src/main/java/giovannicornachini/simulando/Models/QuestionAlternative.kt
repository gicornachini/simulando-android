package giovannicornachini.simulando.Models

import android.text.Html
import android.text.Spanned
import java.io.Serializable

/**
 * Created by giovannicornachini on 24/09/17.
 */
class QuestionAlternative (val id:Int, val text: String, val letter: String): Serializable {

    val textHtmlToView: Spanned?
        get() = Html.fromHtml(this.text)


}