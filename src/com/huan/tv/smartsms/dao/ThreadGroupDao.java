package com.huan.tv.smartsms.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.huan.tv.smartsms.global.Constant;

/**
 * 处理thread_group表的工具类。
 * @author MrTaoge
 *
 */
public class ThreadGroupDao {
	/**
	 * 当前的会话条目是否在某个分组中。
	 * @param contentResolver
	 * @param threadId 会话在表中的对应id。
	 * @return
	 */
	public static boolean isInGroup(ContentResolver contentResolver,int threadId){
		Cursor cursor = contentResolver.query(Constant.URI.URI_THREAD_GROUP_QUERY, null, "thread_id="+threadId, null, null);
		boolean result = false;
		if(cursor!=null&&cursor.moveToNext()){
			result = true;
			cursor.close();
		}
		return result;
	}
	/**
	 * 将当前会话插入到相应的分组下。
	 * @param resolver
	 * @param threadId
	 * @param groupId
	 */
	public static void insertConversataion2GroupById(ContentResolver resolver,int threadId,int groupId){
		ContentValues values = new ContentValues();
		values.put("group_id", groupId);
		values.put("thread_id", threadId);
		resolver.insert(Constant.URI.URI_THREAD_GROUP_INSERT, values);
		//插入操作结束以后要刷新groups数据表。
		int preCount = GroupsDao.getThreadCountByGroupId(resolver, groupId);
		GroupsDao.updateThreadCountByGroupId(resolver, groupId, preCount+1);
	}
	/**
	 * 将指定对话从所属分组中移除。
	 * @param resolver
	 * @param threadId
	 * @param groupId
	 * @return
	 */
	public static int deleteConversationFromGroupById(ContentResolver resolver,int threadId,int groupId){
		int deleteRows = resolver.delete(Constant.URI.URI_THREAD_GROUP_DELETE, "thread_id="+threadId, null);
		//删除以后同样要刷新groups表
		int preCount = GroupsDao.getThreadCountByGroupId(resolver, groupId);
		GroupsDao.updateThreadCountByGroupId(resolver, groupId, preCount-1);
		return deleteRows;
	}
	/**
	 * 通过对话的id获取相应的分组id
	 * @param resolver
	 * @param threadId
	 * @return
	 */
	public static int getGroupIdByThreadId(ContentResolver resolver,int threadId){
		int groupId = -1;
		Cursor cursor = resolver.query(Constant.URI.URI_THREAD_GROUP_QUERY, new String[]{"group_id"}, "thread_id="+threadId, null, null);
		if(cursor!=null&&cursor.moveToNext()){
			groupId = cursor.getInt(0);
			cursor.close();
		}
		return groupId;
	}
	/**
	 * 将某个分组下的所有对话从分组中统统移除。
	 * @param resolver
	 * @param groupId
	 * @return
	 */
	public static int removeAllConversationsByGroupId(ContentResolver resolver,int groupId){
		int deleteRows = resolver.delete(Constant.URI.URI_THREAD_GROUP_DELETE, "group_id="+groupId, null);
		return deleteRows;
	}
}
