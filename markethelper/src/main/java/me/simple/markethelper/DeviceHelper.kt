package me.simple.markethelper

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

object DeviceHelper {

    fun getSystem(): String {
        return Build.MANUFACTURER
    }

    fun getSystemVersion(): String {
        return Build.VERSION.RELEASE
    }

    /**
     * 获取注册过APP_MARKET的全部App
     */
    fun getMarketPkgList(context: Context): List<String> {
        val packageList = mutableListOf<String>()
        val intent = Intent().apply {
            action = "android.intent.action.MAIN"
            addCategory("android.intent.category.APP_MARKET")
        }
        val pm = context.packageManager
        val resolveInfoList = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfoList.isNullOrEmpty()) return packageList
        var packageName = ""
        for (info in resolveInfoList) {
            packageName = info.activityInfo.packageName
            packageList.add(packageName)
        }
        return packageList
    }

    fun getAppName(context: Context): String {
        var appName = ""
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        appName = context.resources.getString(packageInfo.applicationInfo.labelRes)
        return appName
    }
}