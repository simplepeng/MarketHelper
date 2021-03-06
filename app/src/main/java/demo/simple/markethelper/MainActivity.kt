package demo.simple.markethelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*
import me.simple.markethelper.DeviceHelper
import me.simple.markethelper.MarketHelper

class MainActivity : AppCompatActivity() {

    private val mPackageName = "com.tencent.mobileqq"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvSystem.text = String.format("系统：%s", DeviceHelper.getSystem())
        tvSystemVersion.text = String.format("系统版本：%s", DeviceHelper.getSystemVersion())
        val marketList = DeviceHelper.getMarketPkgList(this)
        if (!marketList.isNullOrEmpty()) {
            Log.d(TAG, marketList.toString())
            val builder = StringBuilder()
            for (name in marketList) {
                builder.append(name)
                builder.append("\n")
            }
            builder.append("未注册category.APP_MARKET的商店搜索不到")
            tvMarketList.text = String.format("应用商店：\n%s", builder.toString())
        }

        btnOpen.setOnClickListener {
            val e: Exception? = MarketHelper.open(this, mPackageName)
            if (e != null) {
                Toast.makeText(this, "跳转应用商店失败", Toast.LENGTH_SHORT).show()
            }
            setErrorMessage(e)
        }

        btnOpenBySystem.setOnClickListener {
            val e = MarketHelper.openBySystem(this, mPackageName)
            setErrorMessage(e)
        }

        btnOpenTheFirst.setOnClickListener {
            val e = MarketHelper.openByFirst(this, mPackageName)
            setErrorMessage(e)
        }

        btnOpenByMatch.setOnClickListener {
            val e = MarketHelper.openByMatch(this, mPackageName)
            setErrorMessage(e)
        }

        for (view in llBtns.children) {
            view.setOnClickListener { clickView(it) }
        }
    }

    private fun clickView(view: View) {
        var e: Exception? = null
        when (view.id) {
            R.id.btn360 -> {
                e = MarketHelper.openMarket(this, MarketHelper.QIHOO_360, mPackageName)
            }
            R.id.btnYYB -> {
                e = MarketHelper.openMarket(this, MarketHelper.YING_YONG_BAO, mPackageName)
            }
            R.id.btnWDJ -> {
                e = MarketHelper.openMarket(this, MarketHelper.WAN_DOU_JIA, mPackageName)
            }
            R.id.btnKA -> {
                e = MarketHelper.openMarket(this, MarketHelper.KU_AN, mPackageName)
            }
            R.id.btnYYH -> {
                e = MarketHelper.openMarket(this, MarketHelper.YING_YONG_HUI, mPackageName)
            }
        }
        setErrorMessage(e)
    }

    private fun setErrorMessage(e: Exception?) {
        if (e == null) return
        val message = e.message
        tvErrorMessage.text = String.format("错误信息：\n%s", message)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
