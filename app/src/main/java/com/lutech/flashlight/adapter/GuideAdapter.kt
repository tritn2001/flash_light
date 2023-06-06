package com.lutech.flashlight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lutech.flashlight.R
import com.lutech.flashlight.callback.OnItemClickListener
import com.lutech.flashlight.model.Guide
import kotlinx.android.synthetic.main.item_guide.view.*

class GuideAdapter(
    private val mContext: Context,
    private val mListGuide: ArrayList<Guide>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<GuideAdapter.GuideVH>() {

    inner class GuideVH(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideVH {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_guide,parent,false)
        return GuideVH(view)
    }

    override fun onBindViewHolder(holder: GuideVH, position: Int) {
        val guide = mListGuide[position]

        holder.itemView.apply {
            tvContent.text = mContext.getString(guide.title)
            this.setOnClickListener {
                onItemClickListener.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mListGuide.size
    }
}