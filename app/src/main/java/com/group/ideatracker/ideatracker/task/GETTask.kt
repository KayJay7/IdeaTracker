package com.group.ideatracker.ideatracker.task

import android.os.AsyncTask
import android.util.Log
import com.group.ideatracker.ideatracker.R
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * Created by alvis on 11/02/2018.
 */
class GETTask : AsyncTask<String, Void, JSONObject>() {

    companion object {
        val TAG = GETTask::class.java.simpleName
    }

    override fun doInBackground(vararg p0: String?): JSONObject {

        try {
            val urlConnection = URL("http://192.168.1.8:8000").openConnection() as HttpURLConnection
            urlConnection.setRequestProperty("Content-Type", "application/json")
            urlConnection.requestMethod="GET"
            urlConnection.doOutput = true

            Log.d(TAG,"connecting")

            //urlConnection.connect()

            Log.d(TAG,"connected")

            val scanner = Scanner(urlConnection.inputStream)
            //val json =

            //Log.d(TAG, json.toString())
            try {
                return JSONObject(scanner.nextLine())
            } finally {
                scanner.close()
                urlConnection.inputStream.close()
            }
        } catch (exc: MalformedURLException) {
            Log.wtf(TAG, "URL non valido")

            val json=JSONObject()

            json.apply{
                put("status",false)
                put("message", R.string.invalid_url)
            }

            return json

        } catch (exc: IOException) {
            Log.e(TAG, "Connessione non riuscita")

            val json=JSONObject()
            json.apply{
                put("status",false)
                put("message", R.string.cant_connect)
            }
            return json
        }
    }
}