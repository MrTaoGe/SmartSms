package com.huan.tv.smartsms.broadcast;

import com.huan.tv.smartsms.utils.UIUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsSendReceiver extends BroadcastReceiver {
	public static final String ACTION_SEND_SMS = "com.huan.tv.smartsms.SendSMS";
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent!=null&&getResultCode()==Activity.RESULT_OK){
			UIUtil.showToast("发送成功");
		}else{
			UIUtil.showToast("发送失败");
		}
	}

}
