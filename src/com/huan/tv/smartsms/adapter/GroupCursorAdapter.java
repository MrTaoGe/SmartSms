package com.huan.tv.smartsms.adapter;

import com.huan.tv.smartsms.R;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * 分组界面的适配器
 * @author MrTaoge
 *
 */
public class GroupCursorAdapter extends CursorAdapter {

	public GroupCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return View.inflate(context, R.layout.adapter_group, null);
	}

	private ViewHolder holder;
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		holder = getHolder(view);
		String name = cursor.getString(cursor.getColumnIndex("name"));
		long date = cursor.getLong(cursor.getColumnIndex("createdate"));
		int count = cursor.getInt(cursor.getColumnIndex("thread_count"));
		if(DateUtils.isToday(date)){
			holder.tv_time.setText(DateFormat.getTimeFormat(context).format(date));
		}else{
			holder.tv_name.setText(DateFormat.getDateFormat(context).format(date));
		}
		holder.tv_name.setText(name+"("+count+")");
	}
	
	class ViewHolder{
		private TextView tv_name,tv_time;
		public ViewHolder(View convertView){
			tv_name = (TextView) convertView.findViewById(R.id.tv_group_name);
			tv_time = (TextView) convertView.findViewById(R.id.tv_createtime);
		}
	}
	public ViewHolder getHolder(View convertView){
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if(holder==null){
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		return holder;
	}
}
