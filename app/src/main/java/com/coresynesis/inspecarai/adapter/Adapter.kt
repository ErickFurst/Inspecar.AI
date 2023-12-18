package com.coresynesis.inspecarai.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.coresynesis.inspecarai.R
import com.coresynesis.inspecarai.models.PredicaoModel


class InspecarAdapter(private val context: Context,
                    private val dataSource: ArrayList<PredicaoModel>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.row_inspecar, parent, false)
        val txtViewPredCarAut = rowView.findViewById(R.id.txtViewPredCarAut) as TextView
        val txtViewPredDanoAut = rowView.findViewById(R.id.txtViewPredDanoAut) as TextView
        val txtViewPredTipoAut = rowView.findViewById(R.id.txtViewPredTipo) as TextView
        val txtViewPredSeverAut = rowView.findViewById(R.id.txtViewPredTipo) as TextView
        val txtViewPredEstiloAut = rowView.findViewById(R.id.txtViewPredTop) as TextView
        val imgViewPredCar = rowView.findViewById(R.id.imageViewAut) as ImageView
        
        return rowView
    }
}



