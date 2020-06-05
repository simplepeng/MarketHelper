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
    val marketPkgMap = TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)

    const val SAM_SUNG = "samsung"
    const val SONY = "sony"

    private const val DEFAULT_URI_PREFIX = "market://details?id=%s"

    init {
        marketPkgMap["Google"] = "com.android.vending"//Google Pixel-已测试
        marketPkgMap["OnePlus"] = "com.oppo.market"//一加-已测试
        marketPkgMap["Xiaomi"] = "com.xiaomi.market"//小米-有问题，红米-已测试
        marketPkgMap["Meizu"] = "com.meizu.mstore"//魅族-已测试
        marketPkgMap["OPPO"] = "com.oppo.market"//oppo-已测试
        marketPkgMap["vivo"] = "com.bbk.appstore"//vivo-已测试
        marketPkgMap["HUAWEI"] = "com.huawei.appmarket"//华为-已测试，荣耀-已测试
        marketPkgMap[SAM_SUNG] = "com.sec.android.app.samsungapps"//三星
        marketPkgMap["lenovo"] = "com.lenovo.leos.appstore"//联想
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
        exp = openTheFirst(context) ?: return success

        //exp不为空代表跳转首位失败，开始跳转隐式意图选择框
        exp = openByMatch(context) ?: return success

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
        val marketPkg = marketPkgMap[system]

        val uri = getUri(packageName, system)
        return openByAppPkg(context, packageName, uri, marketPkg)
    }

    /**
     * 打开系统商店列表的首位
     */
    fun openTheFirst(
        context: Context,
        packageName: String = context.packageName
    ): Exception? {
        val marketPkgList = DeviceHelper.getMarketPkgList(context)
        if (marketPkgList.isNullOrEmpty()) return NullPointerException("Market List Is Empty")
        val uri = getUri(packageName)
        return openByAppPkg(context, packageName, uri, marketPkgList.first())
    }

    /**
     * 隐式意图打开应用商店列表弹框
     * 未注册category.APP_MARKET的商店搜索不到
     */
    fun openByMatch(
        context: Context,
        packageName: String = context.packageName
    ): Exception? {
        val uri = getUri(context.packageName)
        return openByAppPkg(context, packageName, uri)
    }

    /**
     * 用App包名，商店包名直接打开
     * 对应App的详情页
     */
    private fun openByAppPkg(
        context: Context,
        packageName: String = context.packageName,
        uri: Uri = getUri(packageName),
        marketPkg: String? = null
    ): Exception? {
        try {
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                addCategory(Intent.CATEGORY_APP_MARKET)
//                addCategory("android.intent.category.BROWSABLE")
                marketPkg?.let { setPackage(it) }
            }
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
    private fun getUriPrefix(system: String? = null): String = when (system) {
        SAM_SUNG -> "http://apps.www.samsungapps.com/appquery/appDetail.as?appId=%s"
        SONY -> "http://m.sonyselect.cn/%s"
        else -> DEFAULT_URI_PREFIX
    }

    /**
     *
     */
    fun getUri(
        packageName: String,
        system: String? = null
    ) = Uri.parse(String.format(getUriPrefix(system), packageName))

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

    /**
     * 打开指定的应用商店
     * 可用来跳转第三方的应用商店，比如：应用宝，酷安等
     * MarketHelper内置了一些第三方商店的包名，在顶部查找
     */
    fun openMarket(
        context: Context,
        packageName: String = context.packageName,
        marketPkg: String
    ): Exception? {
        val uri = Uri.parse(String.format(DEFAULT_URI_PREFIX, packageName))
        return openByAppPkg(context, packageName, uri, marketPkg)
    }
}