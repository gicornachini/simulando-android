package giovannicornachini.vocesabia

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_lead.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

/**
 * A lead capture screen.
 */
class LeadCapture : Activity() {
    private var mAuthTask: LeadCaptureTask? = null
    private val LOG_TAG = "LeadCapture"
    val simulandoAPI = SimulandoAPIHelper.api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead)
        email_sign_in_button.setOnClickListener { attemptCollectLead() }
    }

    private fun attemptCollectLead() {
        if (mAuthTask != null) {
            return
        }

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            showProgress(true)
            mAuthTask = LeadCaptureTask(emailStr)
            mAuthTask!!.execute(null as Void?)
        }
    }

    private fun showAlertWithTwoButton() {
        val alert = CustomDialog(this,
                                 getString(R.string.collect_lead_dialog_title),
                                 getString(R.string.thanks_message))
        alert.showCancelBtn(false)
        alert.setConfirmButton(getString(R.string.message_ok),
                View.OnClickListener {
                    alert.dismiss()
                    this.finish()
                })
        alert.show()
    }

    /**
     * Shows the progress UI and hides the lead capture form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            lead_capture_form.visibility = if (show) View.GONE else View.VISIBLE
            lead_capture_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            lead_capture_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            lead_capture_progress.visibility = if (show) View.VISIBLE else View.GONE
            lead_capture_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            lead_capture_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            lead_capture_progress.visibility = if (show) View.VISIBLE else View.GONE
            lead_capture_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    /**
     * Represents an asynchronous lead capture.
     */
    inner class LeadCaptureTask internal constructor(private val mEmail: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            var success = false
            try {
                success = leadCapture(mEmail)
            } catch (e: SocketTimeoutException) {
                return success
            }

            return success
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null
            showProgress(false)

            if (success!!) {
                showAlertWithTwoButton()
            } else {
                Snackbar.make(findViewById(R.id.lead_capture_form), getString(R.string.error_timeout),
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
                email.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }

        fun leadCapture(email: String): Boolean {
            val call = simulandoAPI.leadCapture(email)

            var response = call.execute()
            if (response.isSuccessful){
                Log.d(LOG_TAG, "Lead Captured")
                return true
            }

            Snackbar.make(findViewById(R.id.startBtn), getString(R.string.error_timeout),
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
            return false
        }
    }
}
