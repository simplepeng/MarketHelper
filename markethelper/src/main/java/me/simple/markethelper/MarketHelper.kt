package me.simple.markethelper

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.lang.Exception
import java.lang.NullPointerException
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
    private val marketPkgMap = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)

    private const val SAM_SUNG = "samsung"
    private const val LE_TV = "letv"
    private const val SONY = "sony"

    private const val DEFAULT_URI_PREFIX = "market://details?id=%s"

    /**
     * 特殊的机型-包名-启动类名
     * 三星->com.sec.android.app.smsungapps-com.sec.android.app.smsungapps
     * 索尼->
     * 乐视->
     */

    init {
        marketPkgMap["Google"] = "com.android.vending"//Google Pixel-已测试
        marketPkgMap["OnePlus"] = "com.heytap.market"//一加-已测试
        marketPkgMap["Xiaomi"] = "com.xiaomi.market"//小米-有问题，红米-已测试
        marketPkgMap["Meizu"] = "com.meizu.mstore"//魅族-已测试
        marketPkgMap["OPPO"] = "com.oppo.market"//oppo-已测试
        marketPkgMap["vivo"] = "com.bbk.appstore"//vivo-已测试
        marketPkgMap["HUAWEI"] = "com.huawei.appmarket"//华为-已测试，荣耀-已测试
        marketPkgMap[SAM_SUNG] = "com.sec.android.app.samsungapps"//三星
        marketPkgMap["lenovo"] = "com.lenovo.leos.appstore"//联想-已测试
        marketPkgMap[LE_TV] = "com.letv.app.appstore"//乐视
        marketPkgMap[""] = ""//红魔
        marketPkgMap[""] = ""//黑鲨
        marketPkgMap[""] = ""//realme
        marketPkgMap[""] = ""//海信
        marketPkgMap[SONY] = ""//索尼
    }

    /**
     * 1. 先直接跳转系统内置的商店，无法跳转就执行第2步
     * 2. 查询系统已经安装的商店然后跳转第一个，已安装商店列表为空或报错执行第3步
     * 3. 调用隐式意图打开应用列表选择框，跳转失败则返回最终的Exception
     */
    fun open(
            context: Context,
            packageName: String = context.packageName
    ): Exception? {
        val success = null

        var exp: Exception? = null
        //先直接跳转系统内置的商店
        exp = openBySystem(context, packageName) ?: return success

        //exp不为空代表跳转内置商店失败，开始跳转首位
        exp = openByFirst(context, packageName) ?: return success

        //exp不为空代表跳转首位失败，开始跳转隐式意图选择框
        exp = openByMatch(context, packageName) ?: return success

        return exp
    }

    /**
     * 打开系统内置的应用商店
     */
    fun openBySystem(
            context: Context,
            packageName: String = context.packageName
    ): Exception? {
        val system = DeviceHelper.getSystem()
//        val system = SAM_SUNG
        val marketPkg = marketPkgMap[system]
        val intent = getMarketIntent(system, packageName, marketPkg)
        return startOpen(context, intent)
    }

    /**
     * 打开系统商店列表的首位
     */
    fun openByFirst(
            context: Context,
            packageName: String = context.packageName
    ): Exception? {
        val marketPkgList = DeviceHelper.getMarketPkgList(context)
        if (marketPkgList.isNullOrEmpty()) return NullPointerException("Market List Is Empty")
        val intent = getDefaultMarketIntent(packageName, marketPkgList.first())
        return startOpen(context, intent)
    }

    /**
     * 隐式意图打开应用商店列表弹框
     * 未注册category.APP_MARKET的商店搜索不到
     */
    fun openByMatch(
            context: Context,
            packageName: String = context.packageName
    ): Exception? {
        val intent = getDefaultMarketIntent(packageName, null)
        return startOpen(context, intent)
    }

    /**
     *
     */
    private fun startOpen(
            context: Context,
            intent: Intent
    ): Exception? {
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            return e
        }
        return null
    }

    /**
     *
     */
    private fun getMarketIntent(
            system: String?,
            packageName: String,
            marketPkg: String?
    ) = when (system) {
        SAM_SUNG -> {
            getSamSungMarketIntent(packageName)
        }
        else -> {
            getDefaultMarketIntent(packageName, marketPkg)
        }
    }

    /**
     * 创建默认的Intent
     */
    private fun getDefaultMarketIntent(
            packageName: String,
            marketPkg: String?
    ): Intent {
        val uri = Uri.parse(String.format(DEFAULT_URI_PREFIX, packageName))
        return Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            marketPkg?.let { setPackage(it) }
        }
    }

    /**
     * 创建三星的Intent
     */
    private fun getSamSungMarketIntent(
            packageName: String
    ): Intent {
        val uri = Uri.parse(String.format("http://apps.samsung.com/appquery/appDetail.as?appId=%s", packageName))
        return Intent(Intent.ACTION_VIEW, uri).apply {
//            setClassName("com.sec.android.app.smsungapps", "com.sec.android.app.smsungapps.Main")
//            data = Uri.parse(String.format("http://apps.samsung.com/appquery/appDetail.as?appId=%s", packageName))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * 打开指定的应用商店
     * 可用来跳转第三方的应用商店，比如：应用宝，酷安等
     * MarketHelper内置了一些第三方商店的包名，在顶部查找
     */
    fun openMarket(
            context: Context,
            marketPkg: String,
            packageName: String = context.packageName
    ): Exception? {
        val intent = getDefaultMarketIntent(packageName, marketPkg)
        return startOpen(context, intent)
    }

    /**
     * 用应用名称去商店检索对应App
     * 较少用，还要手点一下才能搜索
     */
    fun openByAppName(
            context: Context,
            appName: String = DeviceHelper.getAppName(context),
            marketPkg: String? = null
    ): Exception? {
        try {
            val uri = Uri.parse(String.format("market://search?q=%s", appName))
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                marketPkg?.let { setPackage(it) }
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            return e
        }
        return null
    }
}