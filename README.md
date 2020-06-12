# MarketHelper

跳转应用商店的帮助类，对应系统跳转对应的应用商店，小米->小米应用商店，华为-华为市场....

## 依赖

```groovy
implementation "me.simple:MarketHelper:1.0.1"
```

## 使用

直接调用`open`方法的执行逻辑：

1. 先直接跳转系统内置的商店，无法跳转就执行第2步
2. 查询系统已经安装的商店然后跳转第一个，已安装商店列表为空或报错执行第3步
3. 调用隐式意图打开应用列表选择框，跳转失败则返回最终的Exception

```kotlin
    val e: Exception? = MarketHelper.open(this)
    if (e != null) {
        Toast.makeText(this, "跳转应用商店失败", Toast.LENGTH_SHORT).show()
    }
```

### 其他方法

```kotlin
    /**
     * 打开系统内置的应用商店
     */
    fun openBySystem(
        context: Context,
        packageName: String = context.packageName
    )

    /**
     * 打开系统商店列表的首位
     */
    fun openByFirst(
        context: Context,
        packageName: String = context.packageName
    )
		
    /**
     * 隐式意图打开应用商店列表弹框
     * 未注册category.APP_MARKET的商店搜索不到
     */
    fun openByMatch(
        context: Context,
        packageName: String = context.packageName
    )

    /**
     * 用应用名称去商店检索对应App
     * 较少用，还要手点一下才能搜索
     */
    fun openByAppName(
        context: Context,
        appName: String = DeviceHelper.getAppName(context),
        marketPkg: String? = null
    )

    /**
     * 打开指定的应用商店
     * 可用来跳转第三方的应用商店，比如：应用宝，酷安等
     * MarketHelper内置了一些第三方商店的包名，在顶部查找
     */
    fun openMarket(
        context: Context,
     	  marketPkg: String,
        packageName: String = context.packageName
    )
```

## 测试情况

|     机型     |            商店包名             |    测试情况     |
| :----------: | :-----------------------------: | :-------------: |
| Google Pixel |       com.android.vending       |     已通过      |
|  一加，OPPO  |         com.oppo.market         |     已通过      |
|  小米，红米  |        com.xiaomi.market        |     已通过      |
|    Meizu     |        com.meizu.mstore         |     已通过      |
| HUAWEI，荣耀 |      com.huawei.appmarket       |     已通过      |
|     vivo     |        com.bbk.appstore         |     已通过      |
|     三星     | com.sec.android.app.samsungapps | 特殊处理/已通过 |
|     联想     |    com.lenovo.leos.appstore     |     已通过      |
|     乐视     |      com.letv.app.appstore      |     未测试      |
|  待新增...   |                                 |                 |

有其他机型手机的小伙伴下载下面的二维码帮忙测试反馈下，谢谢！

https://www.pgyer.com/markethelper

![qr_code_markethelper](files/qr_code_markethelper.png)

## 版本迭代

* v1.0.0：首次上传