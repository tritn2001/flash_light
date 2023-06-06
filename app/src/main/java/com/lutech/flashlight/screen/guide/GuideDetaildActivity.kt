package com.lutech.flashlight.screen.guide

import android.os.Bundle
import android.text.Html
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.model.Guide
import kotlinx.android.synthetic.main.activity_guide.*
import kotlinx.android.synthetic.main.activity_guide.ivBack
import kotlinx.android.synthetic.main.activity_guide_detaild.*

class GuideDetaildActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_detaild)

        initView()

        ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initView() {
        val mGuide = intent.getSerializableExtra(Constants.GUIDE) as Guide?

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        tvQuestion.text = getString(mGuide!!.questions)
        tvAnswer.text = getString(mGuide!!.answers)



    }
}