package com.push.demo

import android.app.Application
import cn.jpush.android.api.JPushInterface

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/5/15 16:16
 * @UpdateRemark:   更新说明：
 */
class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 初始化极光推送SDK
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
    }
}