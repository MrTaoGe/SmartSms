package com.huan.tv.smartsms.global;

import com.huan.tv.smartsms.dao.provider.GroupContentProvider;

import android.net.Uri;

public interface Constant {
	/**"对话"页面底部的菜单栏向下移动*/
	public int MENU_BAR_TO_DOWN = 0;
	/**"对话"页面底部的菜单栏向上移动*/
	public int MENU_BAR_TO_UP = 1;
	
	public interface URI{
		/**操作对话表的uri*/
		Uri uri_conversation = Uri.parse("content://sms/conversations");
		/**操作短信表的uri*/
		Uri uri_sms = Uri.parse("content://sms");
		
		Uri URI_GROUP_INSERT = Uri.parse(GroupContentProvider.BASE_URI+"/groups/insert");
		Uri URI_GROUP_DELETE = Uri.parse(GroupContentProvider.BASE_URI+"/groups/delete");
		Uri URI_GROUP_UPDATE = Uri.parse(GroupContentProvider.BASE_URI+"/groups/update");
		Uri URI_GROUP_QUERY = Uri.parse(GroupContentProvider.BASE_URI+"/groups/query");
		
		Uri URI_THREAD_GROUP_INSERT = Uri.parse(GroupContentProvider.BASE_URI+"/thread_group/insert");
		Uri URI_THREAD_GROUP_DELETE = Uri.parse(GroupContentProvider.BASE_URI+"/thread_group/delete");
		Uri URI_THREAD_GROUP_UPDATE = Uri.parse(GroupContentProvider.BASE_URI+"/thread_group/update");
		Uri URI_THREAD_GROUP_QUERY = Uri.parse(GroupContentProvider.BASE_URI+"/thread_group/query");
	}
	
	public interface SMSTYPE{
		int TYPE_RECEIVE = 1;
		int TYPE_SEND = 2;
	}
}
