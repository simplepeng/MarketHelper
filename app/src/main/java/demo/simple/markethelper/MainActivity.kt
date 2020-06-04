package demo.simple.markethelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import me.simple.markethelper.DeviceHelper
import me.simple.markethelper.MarketHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvSystem.text = String.format("系统：%s", DeviceHelper.getSystem())
        tvSystemVersion.text = String.format("系统版本：%s", DeviceHelper.getSystemVersion())
        val marketList = DeviceHelper.getMarketList(this)
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

        btnOpenBySystem.setOnClickListener {
            MarketHelper.openBySystem(this)
        }
        btnOpenByMatch.setOnClickListener {
            MarketHelper.openByMatch(this)
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
