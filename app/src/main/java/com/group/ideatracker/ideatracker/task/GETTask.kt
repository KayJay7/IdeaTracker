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

        var json = JSONObject()

        try {
            val urlConnection = URL("http://192.168.0.107:8000/${p0[0]}/?$params").openConnection() as HttpURLConnection
            urlConnection.setRequestProperty("Content-Type", "application/json")
            urlConnection.requestMethod = "GET"
            //urlConnection.doOutput = true

            Log.d(TAG, "connecting")

            val scanner = Scanner(urlConnection.inputStream)


            json = JSONObject(scanner.nextLine())

            scanner.close()
            urlConnection.inputStream.close()

        } catch (exc: MalformedURLException) {

            json.apply {
                put("status", false)
                put("message", R.string.invalid_url)
                put("drawable", R.drawable.ic_web)
            }

        } catch (io: IOException) {

            json.apply {
                put("status", false)
                put("message", R.string.cant_connect)
                put("drawable", R.drawable.ic_lan_disconnect)
            }

        }

        return json
    }

    init {
        val builder = StringBuilder()

        for ((key) in hashMap)
            builder.append(key, "=${hashMap[key]}&")

        builder.setLength(builder.length - 1)

        params = builder.toString()
    }


}