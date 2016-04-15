package com.huan.tv.smartsms.dao;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.huan.tv.smartsms.broadcast.SmsSendReceiver;
import com.huan.tv.smartsms.global.Constant;

/**
 * 发送短信的实体类。
 * @author MrTaoge
 *
 */
public class SmsDao {
	public static void sendSms(Context context,String address,String content){
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> parts = smsManager.divideMessage(content);
//		ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
		Intent intent = new Intent(SmsSendReceiver.ACTION_SEND_SMS);
		PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//		sentIntents.add(sentIntent);
		for(String sms:parts){
			smsManager.sendTextMessage(address, null, sms, sentIntent, null);
			ContentValues values = new ContentValues();
			values.put("address", address);
			values.put("body", content);
			values.put("type", Constant.SMSTYPE.TYPE_SEND);
			refreshSmsDataTalbe(context.getContentResolver(),values);//发送成功以后，短信发送端的相应数据表并没有被自动更新，所以要手动的更新数据。
		}
//		smsManager.sendMultipartTextMessage(address, null, parts, sentIntents, null);
	}
	/**
	 * <strong>刷新短信<u>发送端</u>的数据库中的相应列表。</strong>
	 * <p>发送成功以后，短信<b>发送端</b>的相应数据表并没有被自动更新，所以要手动的更新数据。</p>
	 * @param contentResolver
	 * @param values
	 */
	private static void refreshSmsDataTalbe(ContentResolver contentResolver, ContentValues values) {
		contentResolver.insert(Constant.URI.uri_sms, values);
	}
}
