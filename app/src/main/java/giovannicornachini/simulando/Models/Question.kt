package giovannicornachini.simulando.Models

import android.text.Html
import android.text.Spanned
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Created by giovannicornachini on 19/09/17.
 */
class Question (@SerializedName("id") val id:Int,
                @SerializedName("text") val text: String,
                @SerializedName("question_choices") val alternatives: List<QuestionAlternative>,
                @SerializedName("correct_choice") val correctChoiceId: Int): Serializable {

    val textHtmlToView: Spanned?
        get() = Html.fromHtml(this.text)


}