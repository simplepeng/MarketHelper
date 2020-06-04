package me.simple.markethelper

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.lang.Exception
import java.util.*

object MarketHelper {

    //第三方市场的包名
    const val QIHOO_360 = "com.qihoo.appstore"//360市场
    const val YING_YONG_HUI = "com.yingyonghui.market"//应用汇
    const val YING_YONG_BAO = "com.tencent.android.qqdownloader"//应用宝
    const val BAI_DU = "com.baidu.appsearch"//百度手机助手
    const val WAN_DOU_JIA = "com.wandoujia.phoenix2"//豌豆荚
    const val AN_ZHI = "cn.goapk.market"//安智市场
    const val KU_AN = "com.coolapk.market"//酷安

    //key=系统，value=商店包名
    val marketMap = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)

    init {
        marketMap["Google"] = "com.android.vending"//Google Pixel-已测试
        marketMap["OnePlus"] = "com.oppo.market"//一加-已测试
        marketMap["Xiaomi"] = "com.xiaomi.market"//小米，红米-已测试
        marketMap["Meizu"] = "com.meizu.mstore"//魅族-已测试
        marketMap["OPPO"] = "com.oppo.market"//oppo-已测试
        marketMap["HUAWEI"] = "com.huawei.appmarket"//华为，荣耀-已测试
        marketMap["SAMSUNG"] = "com.sec.android.app.samsungapps"//三星
        marketMap["VIVO"] = "com.bbk.appstore"//vivo
        marketMap["lenovo"] = "com.lenovo.leos.appstore"//联想
        marketMap[""] = ""//红魔
        marketMap[""] = ""//黑鲨
        marketMap[""] = ""//realme
        marketMap[""] = ""//海信
    }

    fun open(context: Context) {

    }

    fun openBySystem(context: Context) {
        val system = DeviceHelper.getSystem()
        val market = marketMap[system]
        if (!market.isNullOrEmpty()) {
            openByMatchWithPackageName(context, context.packageName, market)
        }
//        else {
//            openByMatch(context)
//        }
    }

    fun openByMatch(context: Context) {
        openByMatchWithPackageName(context)
    }

    fun openByMatchWithPackageName(
        context: Context,
        packageName: String = context.packageName,
        market: String? = null
    ) {
        try {
            val uri = Uri.parse(String.format("market://details?id=%s", packageName))
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                market?.let { setPackage(it) }
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openByMatchWithAppName(
        context: Context,
        appName: String = DeviceHelper.getAppName(context),
        market: String? = null
    ) {
        try {
            val uri = Uri.parse(String.format("market://search?q=%s", appName))
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                market?.let { setPackage(it) }
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}