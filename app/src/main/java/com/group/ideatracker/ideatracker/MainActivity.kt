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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

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

        layoutInflater.inflate(R.layout.layout_profile, scrInflateHere)

        supportActionBar?.title = "Nome cognome"
        supportActionBar?.subtitle = "username99"
    }

}

/*
nome cognome username
* totale applicazioni
* totale bug-idee
* bug-idee nell'ultima settimana
* risposte ottenute
*/