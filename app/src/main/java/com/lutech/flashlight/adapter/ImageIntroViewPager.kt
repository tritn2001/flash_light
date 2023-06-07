package com.lutech.flashlight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.getSystemService
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.lutech.flashlight.R
import kotlinx.android.synthetic.main.layout_item_intro.view.*

class ImageIntroViewPager : PagerAdapter {

    var mResources: ArrayList<Int> = ArrayList()

    private var layoutInflater: LayoutInflater? = null

    var mContext: Context? = null


    constructor(mResources: ArrayList<Int>, mContext: Context?) : super() {
        this.mResources = mResources
        this.mContext = mContext
        layoutInflater =
            mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    override fun getCount(): Int {
        return mResources.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val itemView =
            LayoutInflater.from(mContext).inflate(R.layout.layout_item_intro, container, false)
        container.addView(itemView)
        Glide.with(mContext!!).load(mResources[position]).centerCrop().into(itemView.ivIntro)
        return itemView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}