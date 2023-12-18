package com.coresynesis.inspecarai.adapter

import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.coresynesis.inspecarai.R
import com.coresynesis.inspecarai.models.InspecaoModelYolo
import java.util.ArrayList


class QueryAdapterRealTime(val userList: ArrayList<InspecaoModelYolo>) : RecyclerView.Adapter<QueryAdapterRealTime.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.activity_row_inspect_real_time, p0, false)
        return ViewHolder(v)
    }
    override fun getItemCount(): Int {
        return userList.size
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.namePredicaoTipo.text = userList[p1].inspPredTipo.toString()
        p0.namePredicaoConf.text = userList[p1].inspPredConf.toString()
        p0.nameDatahora.text = userList[p1].inspDataHora.toString()

        val bmp = BitmapFactory.decodeByteArray(
            userList[p1].inspPredImg,
            0,
            userList[p1].inspPredImg?.size!!
        )
        p0.nameImagempred.setImageBitmap(bmp)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namePredicaoConf = itemView.findViewById<TextView>(R.id.txtViewPredConf)
        val namePredicaoTipo = itemView.findViewById<TextView>(R.id.txtViewPredTipo)
        val nameImagempred = itemView.findViewById<ImageView>(R.id.imageViewAut)
        val nameDatahora = itemView.findViewById<TextView>(R.id.txtViewDataHoraAut)
    }
}