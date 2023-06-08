package com.lutech.flashlight.screen.guide

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.lutech.flashlight.R
import com.lutech.flashlight.ads.Constants
import com.lutech.flashlight.model.Guide
import kotlinx.android.synthetic.main.activity_guide.ivBack
import kotlinx.android.synthetic.main.activity_guide_detaild.*
import kotlinx.android.synthetic.main.layout_guide_how_to_remove.*
import java.lang.String.format


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

        val s: String = format(context.resources.getString(mGuide.answers))
        tvAnswer.text = getSpannedText(s)

        layout_how_to_remove_in_setting.isVisible = mGuide.isGuidSetting

    }

    private fun getSpannedText(text: String): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(text)
        }
    }

}