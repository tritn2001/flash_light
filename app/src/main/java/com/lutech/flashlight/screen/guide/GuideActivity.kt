package com.lutech.flashlight.screen.guide

import android.content.Intent
import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lutech.flashlight.R
import com.lutech.flashlight.adapter.GuideAdapter
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.callback.OnItemClickListener
import com.lutech.flashlight.model.Guide
import kotlinx.android.synthetic.main.activity_guide.*

class GuideActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var adapter: GuideAdapter

    private lateinit var mList: ArrayList<Guide>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        initView()
        handleEvent()
    }

    private fun handleEvent() {
        ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initView() {
        mList = ArrayList()
        val arrayTitle: TypedArray = resources.obtainTypedArray(R.array.lsTitleGuide)
        val arrayQuestion: TypedArray = resources.obtainTypedArray(R.array.lsQuestionGuide)
        val arrayAnswer: TypedArray = resources.obtainTypedArray(R.array.lsAnswerGuide)

        for (i in 0 until 4) {
            var isGuideSetting = i == 0
            mList.add(
                Guide(
                    arrayTitle.getResourceId(i, 0),
                    arrayQuestion.getResourceId(i, 0),
                    arrayAnswer.getResourceId(i, 0), isGuideSetting
                )
            )
        }

        adapter = GuideAdapter(this, mList, this)

        rvGuide.adapter = adapter
        rvGuide.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }

    override fun onItemClick(pos: Int) {
        val intent = Intent(this, GuideDetaildActivity::class.java)
        intent.putExtra(Constants.GUIDE, mList[pos])
        startActivity(intent)
    }


}