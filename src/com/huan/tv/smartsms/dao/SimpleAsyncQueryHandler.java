package com.huan.tv.smartsms.dao;



import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;

public class SimpleAsyncQueryHandler extends AsyncQueryHandler {

	public SimpleAsyncQueryHandler(ContentResolver contentResolver) {
		super(contentResolver);
	}
	
	@Override
	protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
		super.onQueryComplete(token, cookie, cursor);
		if(cookie!=null&&cookie instanceof CursorAdapter){
			CursorAdapter adapter = (CursorAdapter) cookie;
			adapter.changeCursor(cursor);//相当于notifyDataSetChanged
		}
	}
}
