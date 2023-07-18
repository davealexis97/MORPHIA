package com.morphia.app.textToSpeech

import android.speech.tts.Voice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.morphia.app.databinding.LayoutRvVoicesBinding
import com.morphia.app.modal.VoiceModal


class VoiceAdapter(private val listener: OnItemClick) :
    RecyclerView.Adapter<VoiceAdapter.Holder>() {

    interface OnItemClick {
        fun onVoiceClick(voice: Voice)
    }


    private var list: ArrayList<VoiceModal> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutRvVoicesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val bean = list[holder.adapterPosition]

        holder.binding.tvVoiceName.text = bean.voice.name
        holder.binding.rvRadioButton.isChecked = bean.isSelected

        holder.binding.root.setOnClickListener {
            refresh(holder.adapterPosition)
            listener.onVoiceClick(bean.voice)
        }
    }

    private fun refresh(adapterPosition: Int) {
        list.forEachIndexed { index, voiceModal ->
            voiceModal.isSelected = index == adapterPosition
        }
        notifyDataSetChanged()
    }

    fun setVoices(list: ArrayList<VoiceModal>) {
        this.list = list
        notifyDataSetChanged()
    }

    class Holder(val binding: LayoutRvVoicesBinding) : RecyclerView.ViewHolder(binding.root)
}