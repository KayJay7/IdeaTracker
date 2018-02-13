package com.group.ideatracker.ideatracker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.group.ideatracker.ideatracker.task.GETTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.json.JSONException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val time: Long = 15
        val TAG = MainActivity::class.java.simpleName
    }

    private val startLoading = Runnable {
        progressBar.visibility = View.VISIBLE
        scrInflateHere.alpha = 0.4f
        window.setFlags(16, 16)
    }

    private val stopLoading = Runnable {
        progressBar.visibility = View.GONE
        scrInflateHere.alpha = 1f
        window.clearFlags(16)
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val txv = TextView(this)
        txv.text = "Hello world"

        //scrInflateHere.addView(txv)

        //layoutInflater.inflate(R.layout.layout_profile,scrInflateHere)
        selectFirst()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        (menu.findItem(R.id.actSearch).actionView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                (this@MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(this@MainActivity.currentFocus!!.windowToken, 1)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "QUERY: $newText")
                return false
            }
        })
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.


        when (item.itemId) {
            R.id.profRadio -> {
                scrInflateHere.removeAllViews()
                selectFirst()
            }
            R.id.appRadio -> {
                scrInflateHere.removeAllViews()
                layoutInflater.inflate(R.layout.layout_applications, scrInflateHere)
            }
            R.id.bugsRadio -> {
                scrInflateHere.removeAllViews()
                layoutInflater.inflate(R.layout.layout_bugs, scrInflateHere)
            }
            R.id.nwappButton -> {
                //startGETTask();
            }
            R.id.nwidButton -> {

            }
            R.id.logoutButton -> {
                AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                        .setTitle(R.string.disconnect)
                        .setMessage(R.string.are_you_sure_disconnect)
                        .setPositiveButton(android.R.string.ok, { dialog, _ ->
                            run {

                                val editor = sharedPreferences.edit()
                                editor.clear()

                                editor.apply()

                                dialog.dismiss()
                                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                finish()
                            }
                        }).setNegativeButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                        .setCancelable(false)
                        .show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /*private fun startGETTask() {
        runOnUiThread(startLoading)
        val runnable = Runnable {


            val task = GETTask()
            task.execute()

            try {
                val json = task.get(time, TimeUnit.SECONDS)
                Log.d(TAG, json.toString())
            } catch (exc: TimeoutException) {
                runOnUiThread {
                    AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog)
                            .setTitle(R.string.warning)
                            .setMessage(R.string.timeout_error_message)
                            .setIcon(R.drawable.ic_timer)
                            .setPositiveButton(R.string.retry, { dialog, _ ->
                                run {
                                    startGETTask()
                                    dialog.dismiss()
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                            .setCancelable(false)
                            .show()
                }
            } finally {
                runOnUiThread(stopLoading)
            }
        }

        Thread(Runnable {
            runnable.run()
        }).start()
    }*/

    private fun selectFirst() {

        runOnUiThread(startLoading)
        Thread {
            val pass = sharedPreferences.getString(getString(R.string.preference_passkey), "")
            val usr = sharedPreferences.getString(getString(R.string.preference_username), "")
            val hm = HashMap<String, String>()
            hm["username"] = usr
            hm["password"] = pass
            supportActionBar?.subtitle = usr
            val gt = GETTask(hm)
            gt.execute("user")
            try {
                val json = gt.get(time, TimeUnit.SECONDS)
                Log.d(TAG, "ERROR CODE: $json")

                runOnUiThread(stopLoading)

                runOnUiThread {

                    if (json.getBoolean("status")) {
                        val view = layoutInflater.inflate(R.layout.layout_profile, scrInflateHere)

                        supportActionBar?.title = json.getJSONObject("data").getString("nome") + " " + json.getJSONObject("data").getString("cognome")
                        view.findViewById<TextView>(R.id.txvFirstName).text = json.getJSONObject("data").getString("nome")
                        view.findViewById<TextView>(R.id.txvSurname).text = json.getJSONObject("data").getString("cognome")
                        txvFirstNameSurname.text = (json.getJSONObject("data").getString("nome") + " " + json.getJSONObject("data").getString("cognome"))
                        view.findViewById<TextView>(R.id.txvUsername).text = usr
                        mail.text = json.getJSONObject("data").getString("mail")

                        //layoutInflater.inflate(R.layout.layout_applications,scrInflateHere)
                    } else {
                        try {
                            AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                    .setIcon(json.getInt("drawable"))
                                    .setMessage(json.getInt("message"))
                                    .setTitle(R.string.warning)
                                    .setCancelable(false)
                                    .setPositiveButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                                    .setNegativeButton(R.string.retry, { dialog, _ ->
                                        run {
                                            selectFirst()
                                            dialog.dismiss()
                                        }
                                    })
                                    .show()
                        } catch (jexc: JSONException) {
                            when (json.getInt("errorCode")) {
                                200 -> {
                                    AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                            .setTitle(R.string.warning)
                                            .setIcon(R.drawable.ic_lock)
                                            .setMessage(R.string.wrong_credentials)
                                            .setCancelable(false)
                                            .setPositiveButton(R.string.logout, { dialog, _ ->
                                                run {
                                                    AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                                            .setTitle(R.string.disconnect)
                                                            .setMessage(R.string.are_you_sure_disconnect)
                                                            .setPositiveButton(android.R.string.ok, { dialog, _ ->
                                                                run {

                                                                    val editor = sharedPreferences.edit()
                                                                    editor.clear()

                                                                    editor.apply()

                                                                    dialog.dismiss()
                                                                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                                                    finish()
                                                                }
                                                            }).setNegativeButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                                                            .setCancelable(false)
                                                            .show()
                                                }
                                            })
                                            .setNegativeButton(R.string.retry, { dialog, _ ->
                                                run {
                                                    selectFirst()
                                                    dialog.dismiss()
                                                }
                                            })
                                            .show()
                                }
                                201 -> AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                        .setTitle(R.string.warning)
                                        .setIcon(R.drawable.ic_markunread_mailbox)
                                        .setMessage(R.string.user_needs_confirmation)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.logout, { dialog, _ ->
                                            run {
                                                AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                                        .setTitle(R.string.disconnect)
                                                        .setMessage(R.string.are_you_sure_disconnect)
                                                        .setPositiveButton(android.R.string.ok, { dialog, _ ->
                                                            run {

                                                                val editor = sharedPreferences.edit()
                                                                editor.clear()

                                                                editor.apply()

                                                                dialog.dismiss()
                                                                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                                                finish()
                                                            }
                                                        }).setNegativeButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                                                        .setCancelable(false)
                                                        .show()
                                            }
                                        })
                                        .setNegativeButton(R.string.retry, { dialog, _ ->
                                            run {
                                                selectFirst()
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
                            .setPositiveButton(R.string.logout, { dialog, _ ->
                                run {
                                    AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                                            .setTitle(R.string.disconnect)
                                            .setMessage(R.string.are_you_sure_disconnect)
                                            .setPositiveButton(android.R.string.ok, { dialog, _ ->
                                                run {

                                                    val editor = sharedPreferences.edit()
                                                    editor.clear()

                                                    editor.apply()

                                                    dialog.dismiss()
                                                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                                    finish()
                                                }
                                            }).setNegativeButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                                            .setCancelable(false)
                                            .show()
                                }
                            })
                            .setNegativeButton(R.string.retry, { dialog, _ ->
                                run {
                                    dialog.dismiss()
                                    selectFirst()
                                }
                            })
                            .setCancelable(false)
                            .setIcon(R.drawable.ic_timer)
                            .setTitle(R.string.warning)
                            .show()
                }
            }
        }.start()
        /*supportActionBar?.title = "Nome cognome"
        supportActionBar?.subtitle = "username99"*/
    }

}

/*
nome cognome username
* totale applicazioni
* totale bug-idee
* bug-idee nell'ultima settimana
* risposte ottenute
*/