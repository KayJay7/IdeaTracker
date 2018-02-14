package com.group.ideatracker.ideatracker.recycler

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.group.ideatracker.ideatracker.R
import org.json.JSONArray

/**
 * Created by razvanrosu on 14/02/2018.
 */
class CardAdapter(private var elements: JSONArray, private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_app, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val jsonObject = elements.getJSONObject(position)

        Log.d(CardAdapter::class.java.simpleName, "POS $position")

        holder.layout.findViewById<TextView>(R.id.txvUsername).text = jsonObject.getString("username")
        holder.layout.findViewById<TextView>(R.id.txvAppName).text = jsonObject.getString("appname")
        holder.layout.findViewById<TextView>(R.id.txvMail).text = jsonObject.getString("website")

        // cursor.moveToPosition(position)
        //elements.customizeCard(holder, context, cursor, position)
    }

    override fun getItemCount(): Int {
        return elements.length()
    }

    /*fun updateList(cursor: Cursor) {
        this.cursor = cursor
        notifyDataSetChanged()
    }*/
}