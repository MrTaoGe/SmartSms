package com.huan.tv.smartsms.utils;

import android.util.Log;

public class LogUtil {
	private static boolean isDebug = true;
	public static void e(Object object,String content){
		if(isDebug){
			Log.e(object.getClass().getSimpleName(), content);
		}
	}
	public static void e(String tag,String content){
		if(isDebug){
			Log.e(tag, content);
		}
	}
	public static void i(Object object,String content){
		if(isDebug){
			Log.i(object.getClass().getSimpleName(), content);
		}
	}
	public static void i(String tag,String content){
		if(isDebug){
			Log.i(tag, content);
		}
	}
}
