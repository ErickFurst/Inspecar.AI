package com.coresynesis.inspecarai.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.coresynesis.inspecarai.R
import com.coresynesis.inspecarai.models.PredicaoYoloModel
import java.util.ArrayList


class RealTimeAdapter(val userList: ArrayList<PredicaoYoloModel>) : RecyclerView.Adapter<RealTimeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_ivi_real_time, p0, false)
        return ViewHolder(v)
    }
    override fun getItemCount(): Int {
        return userList.size
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onBindViewHolder(p0: ViewHolder, @SuppressLint("RecyclerView") p1: Int) {

        p0.namePredicaoDano.text = userList[p1].tipodano.toString()
        p0.namePredicaoConf.text = userList[p1].confianca.toString() + "%"
        p0.namePredicaoLeft.text = userList[p1].left.toString()
        p0.namePredicaoTop.text = userList[p1].top.toString()
        p0.namePredicaoRight.text = userList[p1].right.toString()
        p0.namePredicaoBottom.text = userList[p1].bottom.toString()

        p0.spinUsrDano.isEnabled = false
        p0.spinUsrDano.isActivated = false
        p0.spinUsrDano.isClickable = false

        p0.spinUsrDano?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //userList[p1].usrtipodano = p0.spinUsrDano.selectedItem.toString()
                //userList[p1].usrtipodano = parent!!.selectedItem.toString()
                userList[p1].usrtipodano = parent!!.getItemAtPosition(position).toString()
            }

        }

        val bmp = BitmapFactory.decodeByteArray(
            userList[p1].imagem,
            0,
            userList[p1].imagem.size
        )

        p0.nameImagempred.setImageBitmap(bmp)


        p0.swtDiscorda.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                p0.swtDiscorda.text = "Discordo"
                userList[p1].discorda = "Discordo"
                //notifyItemChanged(p0.adapterPosition)
                p0.spinUsrDano.isEnabled = true
                p0.spinUsrDano.isActivated = true
                p0.spinUsrDano.isClickable = true
            }
            else {
                p0.swtDiscorda.text = "Discorda?"
                userList[p1].discorda = "Concordo"
                //notifyItemChanged(p0.adapterPosition)
                p0.spinUsrDano.isEnabled = false
                p0.spinUsrDano.isActivated = false
                p0.spinUsrDano.isClickable = false
            }
        }

        /*
        p0.spinUsrDano.setOnItemClickListener { parent, view, position, id ->
            //userList[p1].usrtipodano = p0.spinUsrDano.selectedItem.toString()
        }
         */


        p0.btnTrash.setOnClickListener(View.OnClickListener {
            userList.removeAt(p0.adapterPosition)
            notifyItemRemoved(p0.adapterPosition)
            notifyItemRangeChanged(p0.adapterPosition, userList.size)

        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namePredicaoDano = itemView.findViewById<TextView>(R.id.txtViewPredDano)
        val namePredicaoConf = itemView.findViewById<TextView>(R.id.txtViewPredTipo)
        val namePredicaoLeft = itemView.findViewById<TextView>(R.id.txtViewPredLeft)
        val namePredicaoTop = itemView.findViewById<TextView>(R.id.txtViewPredTop)
        val namePredicaoRight = itemView.findViewById<TextView>(R.id.txtViewPredRight)
        val namePredicaoBottom = itemView.findViewById<TextView>(R.id.txtViewPredBottom)
        val nameImagempred = itemView.findViewById<ImageView>(R.id.imageViewAut)
        val btnTrash = itemView.findViewById<ImageButton>(R.id.imageButtonTrash)
        val swtDiscorda = itemView.findViewById<Switch>(R.id.switchDiscorda)
        val spinUsrDano = itemView.findViewById<Spinner>(R.id.spinnerUsrDano)

    }


}

