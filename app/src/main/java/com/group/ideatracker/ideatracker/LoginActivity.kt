package com.group.ideatracker.ideatracker

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.group.ideatracker.ideatracker.task.GETTask
import com.group.ideatracker.ideatracker.task.PUTTask
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class LoginActivity : AppCompatActivity() {
    var alreadyHashed = false;

    companion object {
        private const val time: Long = 15
        private val TAG = LoginActivity::class.java.simpleName
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val pass = sharedPreferences.getString(getString(R.string.preference_passkey), "")
        val usr = sharedPreferences.getString(getString(R.string.preference_username), "")
        if (usr.isNotBlank() && pass.isNotBlank()) {
            //startActivity(Intent(this, MainActivity::class.java))
            alreadyHashed = true
            tilUsername.editText!!.setText(usr)
            tilPassword.editText!!.setText(pass)
            login(loginButton)
        }

    }

    private val startLoading = Runnable {
        progressBar.visibility = View.VISIBLE
        mainPage.alpha = 0.4f
        window.setFlags(16, 16)
    }

    private val stopLoading = Runnable {
        progressBar.visibility = View.GONE
        mainPage.alpha = 1f
        window.clearFlags(16)
    }

    fun login(view: View) {

        resetErrors()

        val username = tilUsername.editText!!.text.toString().trim()

        if (username.isBlank())
            tilUsername.error = getString(R.string.fill_field)
        else {

            val password = tilPassword.editText!!.text.toString()

            if (password.isEmpty())
                tilPassword.error = getString(R.string.fill_field)
            else {

                if (newacButton.tag.toString().toBoolean()) {

                    runOnUiThread(startLoading)

                    val runnable = Runnable {
                        val hashMap = HashMap<String, String>()
                        hashMap["username"] = username
                        hashMap["password"] = hashString("SHA-256", password)

                        val task = GETTask(hashMap)
                        task.execute("user")

                        try {
                            val json = task.get(time, TimeUnit.SECONDS)
                            Log.d(TAG, "ERROR CODE: $json")

                            runOnUiThread(stopLoading)

                            runOnUiThread {
                                if (json.getBoolean("status"))
                                    writeAndStart(username, hashMap["password"]!!)
                                else {
                                    try {
                                        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                                .setIcon(json.getInt("drawable"))
                                                .setMessage(json.getInt("message"))
                                                .setTitle(R.string.warning)
                                                .setCancelable(false)
                                                .setPositiveButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                                                .setNegativeButton(R.string.retry, { dialog, _ ->
                                                    run {
                                                        login(view)
                                                        dialog.dismiss()
                                                    }
                                                })
                                                .show()
                                    } catch (jexc: JSONException) {
                                        when (json.getInt("errorCode")) {
                                            200 -> {
                                                tilPassword.error = getString(R.string.wrong_credentials)
                                                tilUsername.error = getString(R.string.wrong_credentials)
                                                tilPassword.editText!!.setText("")
                                                alreadyHashed = false
                                            }
                                            201 -> AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                                    .setTitle(R.string.warning)
                                                    .setIcon(R.drawable.ic_markunread_mailbox)
                                                    .setMessage(R.string.user_needs_confirmation)
                                                    .setCancelable(false)
                                                    .setPositiveButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                                                    .setNegativeButton(R.string.retry, { dialog, _ ->
                                                        run {
                                                            login(view)
                                                            dialog.dismiss()
                                                        }
                                                    })
                                                    .show()

                                        }
                                    }

                                    //Log.e(TAG,"ERROR: $json")
                                }
                            }
                        } catch (exc: TimeoutException) {
                            runOnUiThread(stopLoading)
                            runOnUiThread {
                                AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                        .setMessage(R.string.timeout_error_message)
                                        .setPositiveButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                                        .setNegativeButton(R.string.retry, { dialog, _ ->
                                            run {
                                                dialog.dismiss()
                                                login(view)
                                            }
                                        })
                                        .setCancelable(false)
                                        .setIcon(R.drawable.ic_timer)
                                        .setTitle(R.string.warning)
                                        .show()
                            }
                        }
                    }

                    Thread(runnable).start()

                    //writeAndStart(username, hashMap["password"]!!)

                } else {
                    val rPassword = tilRepeatPassword.editText!!.text.toString()
                    if (rPassword.isEmpty())
                        tilRepeatPassword.error = getString(R.string.fill_field)
                    else if (rPassword.compareTo(password) != 0) {
                        tilPassword.error = getString(R.string.passwords_doesnt_match)
                        tilRepeatPassword.error = getString(R.string.passwords_doesnt_match)
                    } else {
                        val firstName = tilFirstName.editText!!.text.toString().trim()
                        if (firstName.isBlank())
                            tilFirstName.error = getString(R.string.fill_field)
                        else {
                            val surname = tilSurname.editText!!.text.toString().trim()
                            if (surname.isBlank())
                                tilSurname.error = getString(R.string.fill_field)
                            else {
                                val mail = tilMail.editText!!.text.toString().trim()
                                if (mail.isBlank())
                                    tilMail.error = getString(R.string.fill_field)
                                else {
                                    val json = JSONObject()

                                    json.apply {
                                        put("username", username)
                                        put("password", hashString("SHA-256", password))
                                        put("nome", firstName)
                                        put("cognome", surname)
                                        put("mail", mail)
                                    }

                                    runOnUiThread(startLoading)

                                    val runnable = Runnable {
                                        val task = PUTTask(json)
                                        task.execute("user")
                                        try {
                                            val response = task.get(time, TimeUnit.SECONDS)
                                            runOnUiThread {
                                                runOnUiThread(stopLoading)
                                                if (response.getBoolean("status")) {
                                                    AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                                            .setMessage(R.string.user_registered)
                                                            .setCancelable(false)
                                                            .setPositiveButton(android.R.string.ok, { dialog, _ -> dialog.dismiss() })
                                                            .setIcon(R.drawable.ic_mail_outline)
                                                            .setTitle(R.string.done)
                                                            .show()
                                                    change(newacButton)
                                                }

                                                //writeAndStart(username, password)
                                                else {

                                                    try {
                                                        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                                                .setIcon(response.getInt("drawable"))
                                                                .setTitle(R.string.warning)
                                                                .setMessage(response.getInt("message"))
                                                                .setCancelable(false)
                                                                .setPositiveButton(R.string.retry, { dialog, _ ->
                                                                    run {
                                                                        dialog.dismiss()
                                                                        login(view)
                                                                    }
                                                                })
                                                                .setNegativeButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                                                                .show()
                                                    } catch (jexc: JSONException) {
                                                        //TODO handle errors 100 mail already exists and 101 username already exists
                                                        Log.wtf(TAG, "GIMME THE ERROR: $json")

                                                        when (response.getInt("errorCode")) {
                                                            100 -> tilMail.error = getString(R.string.mail_not_valid)
                                                            else -> AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                                                    .setMessage(R.string.error_during_registration)
                                                                    .setCancelable(false)
                                                                    .setPositiveButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                                                                    .show()
                                                        }

                                                    }

                                                }
                                            }
                                        } catch (exc: TimeoutException) {
                                            runOnUiThread(stopLoading)
                                            runOnUiThread {
                                                AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                                        .setMessage(R.string.timeout_error_message)
                                                        .setPositiveButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                                                        .setNegativeButton(R.string.retry, { dialog, _ ->
                                                            run {
                                                                dialog.dismiss()
                                                                login(view)
                                                            }
                                                        })
                                                        .setCancelable(false)
                                                        .setIcon(R.drawable.ic_timer)
                                                        .setTitle(R.string.warning)
                                                        .show()
                                            }
                                        }
                                    }

                                    Thread(runnable).start()

                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private fun hashString(type: String, input: String): String {
        if (alreadyHashed)
            return input
        val HEX_CHARS = "0123456789ABCDEF"
        val bytes = MessageDigest
                .getInstance(type)
                .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }


    private fun writeAndStart(username: String, password: String) {
        val editor = sharedPreferences.edit()

        editor.apply {
            putString(getString(R.string.preference_username), username)
            putString(getString(R.string.preference_passkey), password)
        }
        editor.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun resetErrors() {
        tilUsername.error = null
        tilPassword.error = null
        tilRepeatPassword.error = null
        tilFirstName.error = null
        tilSurname.error = null
        tilMail.error = null
    }


    fun change(view: View) {

        resetErrors()

        if (view.tag.toString().toBoolean()) {

            signupFields.visibility = View.VISIBLE
            tilRepeatPassword.visibility = View.VISIBLE
            txvHelloWorld.text = getString(R.string.signup)
            newacButton.text = getText(R.string.cncl)
            loginButton.text = getText(R.string.signup)
        } else {
            signupFields.visibility = View.GONE
            tilRepeatPassword.visibility = View.GONE
            txvHelloWorld.text = getString(R.string.login)
            newacButton.text = getText(R.string.nwac)
            loginButton.text = getText(R.string.login)
        }
        view.tag = view.tag.toString().toBoolean().not()
    }
}
