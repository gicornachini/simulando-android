package giovannicornachini.vocesabia

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import kotlinx.android.synthetic.main.custom_dialog.*


/**
 * Created by giovannicornachini on 30/12/2017.
 *
 * Reusable custom dialog.
 */
class CustomDialog : Dialog {

    var message: String? = null
    var title: String? = null
    private var confirmBtnText: String? = null
    private var cancelBtnText: String? = null
    private var confirmBtnListener: View.OnClickListener? = null
    private var cancelBtnListener: View.OnClickListener? = null
    private var showCancelBtn: Boolean = true

    constructor(context: Context, message: String, title:String?) : super(context) {
        this.message = message
        this.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)
        titleTxt.text = this.message
        messageTxt.text = this.title

        confirmBtn.text = confirmBtnText
        confirmBtn.setOnClickListener(confirmBtnListener)

        if(!this.showCancelBtn) { cancelBtn.visibility = View.GONE }
        cancelBtn.text = cancelBtnText
        cancelBtn.setOnClickListener(cancelBtnListener)
    }

    fun setConfirmButton(text: String, onClickListener: View.OnClickListener) {
        dismiss()
        this.confirmBtnListener = onClickListener
        this.confirmBtnText = text

    }

    fun setCancelButton(text:String, onClickListener: View.OnClickListener) {
        dismiss()
        this.cancelBtnListener = onClickListener
        this.cancelBtnText = text
    }

    fun showCancelBtn(show: Boolean) {
        this.showCancelBtn = show
    }
}