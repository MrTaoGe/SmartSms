package com.huan.tv.smartsms.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.huan.tv.smartsms.global.Constant;

public class GroupsDao {
	/**
	 * 创建分组。
	 * @param resolver
	 * @param groupName
	 */
	public static void createGroup(ContentResolver resolver,String groupName){
		ContentValues values = new ContentValues();
		values.put("name", groupName);
		values.put("createdate", System.currentTimeMillis());
		values.put("thread_count", 0);
		resolver.insert(Constant.URI.URI_GROUP_INSERT, values);
	} 
	/**
	 * 对指定的分组进行重命名。
	 * @param resolver
	 * @param groupId
	 * @param groupName
	 * @return
	 */
	public static int renameOfGroup(ContentResolver resolver,int groupId,String groupName){
		ContentValues values = new ContentValues();
		values.put("name", groupName);
		int rowIds = resolver.update(Constant.URI.URI_GROUP_UPDATE, values, "_id="+groupId, null);
		return rowIds;
	}
	/**
	 * 根据分组id删除相应的分组名。
	 * @param resolver
	 * @param groupId
	 * @return
	 */
	public static int deleteGroup(ContentResolver resolver,int groupId){
		int deleteRows = resolver.delete(Constant.URI.URI_GROUP_DELETE, "_id="+groupId, null);
		//当分组被删除之后，它下面的对话也应该一并被删除
		ThreadGroupDao.removeAllConversationsByGroupId(resolver, groupId);
		return deleteRows;
	}
	/**
	 * 获取分组名称
	 * @param resolver
	 * @param groupId
	 * @return
	 */
	public static String getGroupNameByGroupId(ContentResolver resolver,int groupId){
		String groupName = "";
		Cursor cursor = resolver.query(Constant.URI.URI_GROUP_QUERY, new String[]{"name"}, "_id="+groupId, null, null);
		if(cursor!=null&&cursor.moveToNext()){
			groupName = cursor.getString(0);
			cursor.close();
		}
		return groupName;
	}
	/**
	 * 获取相应分组下有多少个对话。
	 * @param resolver
	 * @param groupId
	 * @return
	 */
	public static int getThreadCountByGroupId(ContentResolver resolver,int groupId){
		int count = 0;
		Cursor cursor = resolver.query(Constant.URI.URI_GROUP_QUERY, new String[]{"thread_count"}, "_id="+groupId, null, null);
		if(cursor!=null&&cursor.moveToNext()){
			count = cursor.getInt(0);
		}
		return count;
	}
	/**
	 * 更新相应分组下的对话数量。
	 * @param resolver
	 * @param groupId
	 * @param newCount
	 * @return
	 */
	public static int updateThreadCountByGroupId(ContentResolver resolver,int groupId,int newCount){
		ContentValues values = new ContentValues();
		values.put("thread_count", newCount);
		int updateRows = resolver.update(Constant.URI.URI_GROUP_UPDATE, values, "_id="+groupId, null);
		return updateRows;
	}
	/**
	 * 判断分组界面是否已经创建了分组。
	 * @param resolver
	 * @return
	 */
	public static boolean hasCreatedSomeGroups(ContentResolver resolver){
		boolean hasGroups = false;
		Cursor cursor = resolver.query(Constant.URI.URI_GROUP_QUERY, new String[]{"name"}, null, null, null);
		if(cursor!=null&&cursor.moveToNext()){
			hasGroups = true;
			cursor.close();
		}
		return hasGroups;
	}
}
