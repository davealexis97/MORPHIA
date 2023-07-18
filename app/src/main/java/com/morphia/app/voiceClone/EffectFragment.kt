package com.morphia.app.voiceClone


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.morphia.app.BuildConfig
import com.morphia.app.databinding.FragmentEffectBinding
import com.reactlibrary.`object`.EffectObject
import com.reactlibrary.utils.DBLog


class EffectFragment : Fragment(), View.OnClickListener, EffectAdapter.OnItemClick {


    lateinit var binding: FragmentEffectBinding

    private var voiceHelper: VoiceHelper? = null
    private lateinit var adapter: EffectAdapter
    private var fileName: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEffectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        initViews()
        setClicks()
        getEffects()
    }

    private fun getEffects() {
        val effectStream = requireContext().assets.open("effects.json")
        val effects = AudioUtils.getStringFromInputStream(effectStream)
        val effectType = object : TypeToken<ArrayList<Any>>() {}.type
        val effectsList = Gson().fromJson(effects, effectType) as ArrayList<Any>
        effectsList.forEach {
            voiceHelper?.insertEffect(Gson().toJson(it))
        }
        adapter.addEffects(voiceHelper?.getEffects())
    }

    private fun initViews() {
        DBLog.setDebug(BuildConfig.DEBUG)


        voiceHelper = VoiceHelper(requireContext())
        voiceHelper?.setPath(fileName)
        voiceHelper?.createDBMedia()
    }

    private fun setAdapter() {

        binding.rvEffects.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        adapter = EffectAdapter(this)
        binding.rvEffects.adapter = adapter

    }


    private fun setClicks() {

    }


    override fun onEffectSelect(bean: EffectObject, position: Int) {
        voiceHelper?.setPlayingIndex(position)
        voiceHelper?.playEffect(position)
    }

    override fun onClick(v: View?) {

    }


}