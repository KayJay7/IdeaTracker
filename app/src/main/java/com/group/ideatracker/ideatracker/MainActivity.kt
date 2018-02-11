package com.group.ideatracker.ideatracker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
        // R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        scrInflateHere.removeAllViews()

        when (item.itemId) {
            R.id.profRadio -> {

                selectFirst()
            }
            R.id.appRadio -> {


                layoutInflater.inflate(R.layout.layout_applications,scrInflateHere)

            }
            R.id.bugsRadio -> {
                layoutInflater.inflate(R.layout.layout_bugs,scrInflateHere)
            }
            R.id.nwappButton -> {

            }
            R.id.nwidButton -> {

            }
            R.id.logoutButton -> {
                AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                        .setTitle(R.string.disconnect)
                        .setMessage(R.string.are_you_sure_disconnect)
                        .setPositiveButton(android.R.string.ok, { dialog, _ ->
                            run {
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

    private fun selectFirst(){

        layoutInflater.inflate(R.layout.layout_profile,scrInflateHere)

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