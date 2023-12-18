package com.coresynesis.inspecarai.adapter

import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.coresynesis.inspecarai.R
import com.coresynesis.inspecarai.activities.AddCar.Companion.dataList
import com.coresynesis.inspecarai.models.PredicaoModel


class CarAdapter(val userList: ArrayList<PredicaoModel>) : RecyclerView.Adapter<CarAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_inspecar_v2, p0, false)
        return ViewHolder(v)
    }
    override fun getItemCount(): Int {
        return userList.size
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.namePredicaocarro.text = userList[p1].predicaocarro.toString()
        p0.namePredicao.text = userList[p1].predicao.toString()
        p0.namePredicaotipo.text = userList[p1].predicaotipo.toString()
        p0.namePredicaosev.text = userList[p1].predicaosev.toString()
        p0.nameEstilocarro.text = userList[p1].estilocarro.toString()
        val bmp = BitmapFactory.decodeByteArray(
            userList[p1].imagempred,
            0,
            userList[p1].imagempred.size
        )
        p0.nameImagempred.setImageBitmap(bmp)


        p0.swtDiscorda.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                p0.swtDiscorda.text = "Discordo"
                dataList.get(p1).discorda = "Discordo"
            }
            else {
                p0.swtDiscorda.text = "Discorda?"
                dataList.get(p1).discorda = "Concordo"
            }
        }

        p0.btnTrash.setOnClickListener(View.OnClickListener {
            userList.removeAt(p0.adapterPosition)
            notifyItemRemoved(p0.adapterPosition)
            notifyItemRangeChanged(p0.adapterPosition, userList.size)
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namePredicaocarro = itemView.findViewById<TextView>(R.id.txtViewPredCarAut)
        val namePredicao = itemView.findViewById<TextView>(R.id.txtViewPredDanoAut)
        val namePredicaosev = itemView.findViewById<TextView>(R.id.txtViewPredTipo)
        val namePredicaotipo = itemView.findViewById<TextView>(R.id.txtViewPredTipo)
        val nameEstilocarro = itemView.findViewById<TextView>(R.id.txtViewPredTop)
        val nameImagempred = itemView.findViewById<ImageView>(R.id.imageViewAut)
        val btnTrash = itemView.findViewById<ImageButton>(R.id.imageButtonTrash)
        val swtDiscorda = itemView.findViewById<Switch>(R.id.switchDiscorda)
    }
}

