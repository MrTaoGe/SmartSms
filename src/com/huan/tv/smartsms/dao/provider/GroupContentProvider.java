package com.huan.tv.smartsms.dao.provider;

import com.huan.tv.smartsms.dao.db.DBHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
/**
 * 分组页面的内容提供者。
 * @author MrTaoge
 *
 */
public class GroupContentProvider extends ContentProvider {

	private static String authority = "com.huan.tv.smartsms";
	public static Uri BASE_URI = Uri.parse("content://"+authority);
	private static UriMatcher uriMatcher;
	private static DBHelper dbHelper;
	//操作groups表的匹配码。
	public static final int CODE_GROUP_INSERT = 0;
	public static final int CODE_GROUP_DELETE = 1;
	public static final int CODE_GROUP_UPDATE = 2;
	public static final int CODE_GROUP_QUERY = 3;
	
	//操作thread_group表的匹配码。
	public static final int CODE_THREAD_GROUP_INSERT = 4;
	public static final int CODE_THREAD_GROUP_DELETE = 5;
	public static final int CODE_THREAD_GROUP_UPDATE = 6;
	public static final int CODE_THREAD_GROUP_QUERY = 7;
	
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(authority, "groups/insert", CODE_GROUP_INSERT);
		uriMatcher.addURI(authority, "groups/delete", CODE_GROUP_DELETE);
		uriMatcher.addURI(authority, "groups/update", CODE_GROUP_UPDATE);
		uriMatcher.addURI(authority, "groups/query", CODE_GROUP_QUERY);
		
		uriMatcher.addURI(authority, "thread_group/insert", CODE_THREAD_GROUP_INSERT);
		uriMatcher.addURI(authority, "thread_group/delete", CODE_THREAD_GROUP_DELETE);
		uriMatcher.addURI(authority, "thread_group/update", CODE_THREAD_GROUP_UPDATE);
		uriMatcher.addURI(authority, "thread_group/query", CODE_THREAD_GROUP_QUERY);
	}
	
	@Override
	public boolean onCreate() {
		dbHelper = DBHelper.getInstance(getContext());
		return false;
	}



	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case CODE_GROUP_INSERT:
			if(db.isOpen()){
				long rowId = db.insert(DBHelper.T_GROUP, null, values);
				if(rowId!=-1){
					getContext().getContentResolver().notifyChange(BASE_URI, null);
					return Uri.withAppendedPath(BASE_URI, rowId+"");
				}else{
					return null;
				}
			}
			break;
		case CODE_THREAD_GROUP_INSERT:
			if(db.isOpen()){
				long rowId = db.insert(DBHelper.T_THREAD_GROUP, null, values);
				if(rowId!=-1){
					getContext().getContentResolver().notifyChange(BASE_URI, null);
					return Uri.withAppendedPath(BASE_URI, rowId+"");
				}else{
					return null;
				}
			}
			break;

		default:
			throw new IllegalArgumentException("非法的uri："+uri.toString());
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case CODE_GROUP_DELETE:
			if(db.isOpen()){
				int deleteRows = db.delete(DBHelper.T_GROUP, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(BASE_URI, null);
				return deleteRows;
			}
			break;
		case CODE_THREAD_GROUP_DELETE:
			if(db.isOpen()){
				int deleteRows = db.delete(DBHelper.T_THREAD_GROUP, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(BASE_URI, null);
				return deleteRows;
			}
			break;
		default:
			throw new IllegalArgumentException("非法的uri："+uri.toString());
		}
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case CODE_GROUP_UPDATE:
			if(db.isOpen()){
				int updateRows = db.update(DBHelper.T_GROUP, values, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(BASE_URI, null);
				return updateRows;
			}
			break;

		default:
			throw new IllegalArgumentException("非法的uri："+uri.toString());
		}
		return 0;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		switch (uriMatcher.match(uri)) {
		case CODE_GROUP_QUERY:
			if(db.isOpen()){
				Cursor cursor = db.query(DBHelper.T_GROUP, projection, selection, selectionArgs, null, null, sortOrder);
				cursor.setNotificationUri(getContext().getContentResolver(), BASE_URI);
				return cursor;
			}
			break;
		case CODE_THREAD_GROUP_QUERY:
			if(db.isOpen()){
				Cursor cursor = db.query(DBHelper.T_THREAD_GROUP, projection, selection, selectionArgs, null, null, sortOrder);
				cursor.setNotificationUri(getContext().getContentResolver(), BASE_URI);
				return cursor;
			}
			break;
		default:
			throw new IllegalArgumentException("非法的uri："+uri.toString());
		}
		return null;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}
}
