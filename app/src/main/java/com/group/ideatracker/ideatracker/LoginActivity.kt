package com.group.ideatracker.ideatracker

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    //var login=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun login(view:View) {
        val intent= Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun change(view:View){
        //if(login){
        if(view.tag.toString().toBoolean()){
            loginFields.visibility=View.GONE
            signupFields.visibility=View.VISIBLE
            txvHelloWorld.text=getString(R.string.signup)
            newacButton.text=getText(R.string.cncl)
            loginButton.text=getText(R.string.signup)
        }else{
            loginFields.visibility=View.VISIBLE
            signupFields.visibility=View.GONE
            txvHelloWorld.text=getString(R.string.login)
            newacButton.text=getText(R.string.nwac)
            loginButton.text=getText(R.string.login)
        }
        view.tag=view.tag.toString().toBoolean().not()
    }
}
