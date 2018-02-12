package com.group.ideatracker.ideatracker.task

import android.os.AsyncTask
import com.group.ideatracker.ideatracker.R
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * Created by razvanrosu on 12/02/2018.
 */
class PUTTask(val toPut: JSONObject) : AsyncTask<String, Void, JSONObject>() {

    override fun doInBackground(vararg p0: String?): JSONObject {

        var json = JSONObject()

        try {
            val urlConnection = URL("http://192.168.1.105:8000/${p0[0]}/").openConnection() as HttpURLConnection
            urlConnection.setRequestProperty("Content-Type", "application/json")
            urlConnection.requestMethod = "PUT"
            urlConnection.doOutput = true

            val wr = DataOutputStream(urlConnection.outputStream)

            wr.writeBytes(toPut.toString())
            wr.flush()
            wr.close()

            val scanner = Scanner(urlConnection.inputStream)
            json = JSONObject(scanner.nextLine())

            scanner.close()
            urlConnection.inputStream.close()

        } catch (exc: MalformedURLException) {

            json.apply {
                put("status", false)
                put("message", R.string.invalid_url)
                put("drawable", R.drawable.ic_timer)
            }

        } catch (io: IOException) {

            json.apply {
                put("status", false)
                put("message", R.string.cant_connect)
                put("drawable", R.drawable.ic_web)
            }

        }

        return json
    }


}