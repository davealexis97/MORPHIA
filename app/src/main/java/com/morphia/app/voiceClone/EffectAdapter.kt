package com.morphia.app.voiceClone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.morphia.app.databinding.LayoutRvEffectsBinding
import com.reactlibrary.`object`.EffectObject

class EffectAdapter(private val listener: OnItemClick) :
    RecyclerView.Adapter<EffectAdapter.Holder>() {

    var list = arrayListOf<EffectObject>()


    class Holder(val binding: LayoutRvEffectsBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClick {
        fun onEffectSelect(bean: EffectObject, position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutRvEffectsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val bean = list[holder.adapterPosition]

        holder.binding.tvEffectName.text = bean.name
        holder.binding.root.setOnClickListener {
            listener.onEffectSelect(bean, holder.adapterPosition)
        }
    }

    fun addEffects(list: ArrayList<EffectObject>?) {
        if (list != null) {
            this.list = list
            notifyDataSetChanged()
        }

    }


}