package giovannicornachini.simulando.Models

import android.text.Html
import android.text.Spanned
import java.io.Serializable


/**
 * Created by giovannicornachini on 19/09/17.
 */
class Question (val id:Int, val text: String): Serializable {

    val textHtmlToView: Spanned?
        get() = Html.fromHtml(this.text)


}