package com.huan.tv.smartsms.utils;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import com.huan.tv.smartsms.global.MyApplication;
import com.huan.tv.smartsms.ui.dialog.ConversationDialog;
import com.huan.tv.smartsms.ui.dialog.ConversationDialog.OnConversationDialogClickListener;
import com.huan.tv.smartsms.ui.dialog.CreateGroupDialog;
import com.huan.tv.smartsms.ui.dialog.CreateGroupDialog.OnCreateGroupCompleteListener;
import com.huan.tv.smartsms.ui.dialog.DeleteSMSDialog;
import com.huan.tv.smartsms.ui.dialog.DeleteSMSDialog.OnCancelDeleteListener;
import com.huan.tv.smartsms.ui.dialog.HandleGroupDialog.OnHandleGroupDialogClickListener;
import com.huan.tv.smartsms.ui.dialog.HandleGroupDialog;

public class UIUtil {
	
	public static void printCursor(Cursor cursor){
		if(cursor==null)return;
		while(cursor.moveToNext()){
			int columnCount = cursor.getColumnCount();
			for(int i=0; i<columnCount; i++){
				String columnName = cursor.getColumnName(i);
				String columnValue = cursor.getString(i);
				LogUtil.e("UIUtil", columnName+":"+columnValue);
			}
			LogUtil.e("UIUtil", "**************************************");
		}
		cursor.close();
	}
	
	public static Context getContext(){
		return MyApplication.getContext();
	}
	public static void showConversationDialog(Context context,String notice,String content,OnConversationDialogClickListener listener){
		ConversationDialog conversationDialog = new ConversationDialog(context,notice,content,listener);
		conversationDialog.show();
	}
	public static DeleteSMSDialog showDeleteDialog(Context context,int maxProgress,OnCancelDeleteListener listener){
		DeleteSMSDialog deleteSMSDialog = new DeleteSMSDialog(context,maxProgress,listener);
		deleteSMSDialog.show();
		return deleteSMSDialog;
	}
	public static void showCreateGroupDialog(Context context,String title,OnCreateGroupCompleteListener listener){
		CreateGroupDialog createGroupDialog = new CreateGroupDialog(context, title, listener);
		createGroupDialog.show();
	}
	public static void showHandleGroupDialog(Context context,String title,String[] handleNames,OnHandleGroupDialogClickListener listener){
		HandleGroupDialog dialog = new HandleGroupDialog(context, title, handleNames, listener);
		dialog.show();
	}
	
	/**根据资源id获取相应输出信息后弹出吐司*/
	public static void showToast(int res){
		Toast.makeText(getContext(), getContext().getResources().getString(res), 0).show();
	}
	public static void showToast(String content){
		Toast.makeText(getContext(), content, 0).show();
	}
	/**
	 * 将通讯录中带有空格，横线以及括号等特殊字符的号码转化为正常的号码
	 * @param str
	 * @return
	 */
	public static String formatPhoneNumber(String str){
		char[] numberChar = str.toCharArray();
		StringBuilder builder = new StringBuilder();
		int length = numberChar.length;
		for (int i = 0; i < length; i++) {
			if(!Character.isDigit(numberChar[i]))continue;
			builder.append(numberChar[i]);
		}
		return builder.toString();
	}
}
