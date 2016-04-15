package com.huan.tv.smartsms.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "smartsms.db";
	public static final String T_GROUP = "groups";
	private static final Object THREAD_LOCK = DBHelper.class;
	public static final String T_THREAD_GROUP = "thread_group";
	private static DBHelper mInstance;
	
	private DBHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	}

	public static DBHelper getInstance(Context context){
		if(mInstance==null){
			synchronized (THREAD_LOCK) {
				if(mInstance==null)
					mInstance = new DBHelper(context, DB_NAME, null, 1);
			}
		}
		return mInstance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建主表——群组表
		db.execSQL("CREATE TABLE "+T_GROUP+"("//注意表名和前面的宋庆龄语句之间要加个空格。
				+"_id INTEGER PRIMARY KEY," 
				+"name VARCHAR," 
				+"createdate INTEGER," 
				+"thread_count INTEGER)");
		//创建附表——分组表
		db.execSQL("CREATE TABLE "+T_THREAD_GROUP+"(" 
				+"_id INTEGER PRIMARY KEY,"
				+"thread_id INTEGER," 
				+"group_id INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
