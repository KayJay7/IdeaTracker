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
class GETTask(hashMap: HashMap<String, String>) : AsyncTask<String, Void, JSONObject>() {

    companion object {
        val TAG = GETTask::class.java.simpleName
    }

    val params: String


    override fun doInBackground(vararg p0: String?): JSONObject {

        try {
            val urlConnection = URL("http://192.168.1.105:8000/${p0[0]}/").openConnection() as HttpURLConnection
            urlConnection.setRequestProperty("Content-Type", "application/json")
            urlConnection.requestMethod = "GET"
            urlConnection.doOutput = true

            Log.d(TAG, "connecting")

            val scanner = Scanner(urlConnection.inputStream)

            try {
                return JSONObject(scanner.nextLine())
            } finally {
                scanner.close()
                urlConnection.inputStream.close()
            }
        } catch (exc: MalformedURLException) {
            Log.wtf(TAG, "URL non valido")

            val json = JSONObject()

            /*json.apply {
                put("username", false)
                put("message", R.string.invalid_url)
            }*/

            return json

        } catch (exc: IOException) {
            Log.e(TAG, "Connessione non riuscita")

            val json = JSONObject()
            json.apply {
                put("status", false)
                put("message", R.string.cant_connect)
            }
            return json
        }
    }

    init {
        val builder = StringBuilder()

        for ((key) in hashMap)
            builder.append(key, "=${hashMap[key]}")

        builder.setLength(builder.length - 1)

        params = builder.toString()
    }


}