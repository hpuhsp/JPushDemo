package com.push.demo.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import cn.jpush.android.api.*
import cn.jpush.android.service.JPushMessageReceiver
import com.push.demo.MainActivity
import com.push.demo.R
import com.push.demo.TestActivity

/**
 * @Description: 自定义广播接收器
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/5/15 16:04
 * @UpdateRemark:   更新说明：
 */
class MyPushMessageReceiver : JPushMessageReceiver() {
    
    companion object {
        private const val TAG = "PushMessageReceiver"
        private const val CHANNEL_ID = "TestPush"
        private const val CHANNEL_NAME = "TestNotification"
    }
    
    override fun onMessage(context: Context, customMessage: CustomMessage) {
        Log.e(TAG, "[onMessage] $customMessage")
//        val intent = Intent("com.jiguang.demo.message")
//        intent.putExtra("msg", customMessage.message)
//        context.sendBroadcast(intent)
        processCustomSound(context, customMessage)
    }
    
    override fun onNotifyMessageOpened(context: Context, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageOpened] $message")
        try {
            //打开自定义的Activity
            val i = Intent(context, TestActivity::class.java)
            val bundle = Bundle()
            bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, message.notificationTitle)
            bundle.putString(JPushInterface.EXTRA_ALERT, message.notificationContent)
            i.putExtras(bundle)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(i)
        } catch (throwable: Throwable) {
        }
    }
    
    override fun onMultiActionClicked(context: Context?, intent: Intent) {
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮")
        val nActionExtra = intent.extras!!.getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA)
        
        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if (nActionExtra == null) {
            Log.d(TAG, "ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null")
            return
        }
        if (nActionExtra == "my_extra1") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一")
        } else if (nActionExtra == "my_extra2") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二")
        } else if (nActionExtra == "my_extra3") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三")
        } else {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义")
        }
    }
    
    override fun onNotifyMessageArrived(context: Context?, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageArrived] $message")
    }
    
    override fun onNotifyMessageDismiss(context: Context?, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageDismiss] $message")
    }
    
    private fun processCustomSound(context: Context, customMessage: CustomMessage) {
        val intent = Intent(context.applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        intent.putExtras(bundle)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val soundUri: Uri = getSound(context)
        
        // 此处注释代码提供自定义不同类型的通知铃声思路
//        if (!TextUtils.isEmpty(customMessage.extra)) {
//            try {
//                val extraJson = JSON.parseObject(customMessage.extra)
//                val sound = extraJson.getString("sound");
////					if("jpush_s.mp3".equals(sound)){
////						notification.setSound(Uri.parse("android.resource://" + context.getPackageName() +  "/" +id));
//
////					}
//            } catch (e: JSONException) {
//            }
//        }
        
        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
        
        notification.setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setWhen(System.currentTimeMillis())
            .setSound(soundUri, AudioManager.STREAM_NOTIFICATION)
            .setContentText(customMessage.message ?: "")
            .setSmallIcon(R.mipmap.jpush_notification_icon)
            .setContentIntent(pendingIntent)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            if (null != soundUri) {
                // Changing Default mode of notification
                notification.setDefaults(Notification.DEFAULT_VIBRATE)
                // Creating an Audio Attribute
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                val audioAttributes2 = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build()
                channel.setSound(soundUri, audioAttributes2)
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        notificationManager.notify(100, notification.build())
    }
    
    private fun getSound(context: Context): Uri {
        // TODO 根据需要的逻辑进行自定义通知铃声
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.xp
        )
    }
    
    override fun onConnected(context: Context?, isConnected: Boolean) {
        Log.e(TAG, "[onConnected] $isConnected")
    }
    
    override fun onCommandResult(context: Context?, cmdMessage: CmdMessage) {
        Log.e(TAG, "[onCommandResult] $cmdMessage")
    }
    
    override fun onTagOperatorResult(context: Context?, jPushMessage: JPushMessage?) {
        super.onTagOperatorResult(context, jPushMessage)
    }
    
    override fun onCheckTagOperatorResult(context: Context?, jPushMessage: JPushMessage?) {
        super.onCheckTagOperatorResult(context, jPushMessage)
    }
    
    override fun onAliasOperatorResult(context: Context?, jPushMessage: JPushMessage?) {
        super.onAliasOperatorResult(context, jPushMessage)
    }
    
    override fun onMobileNumberOperatorResult(context: Context?, jPushMessage: JPushMessage?) {
        super.onMobileNumberOperatorResult(context, jPushMessage)
    }
    
    override fun onNotificationSettingsCheck(context: Context?, isOn: Boolean, source: Int) {
        super.onNotificationSettingsCheck(context, isOn, source)
        Log.e(TAG, "[onNotificationSettingsCheck] isOn:$isOn,source:$source")
    }
}