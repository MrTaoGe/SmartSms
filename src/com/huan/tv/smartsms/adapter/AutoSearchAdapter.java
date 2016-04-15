package com.huan.tv.smartsms.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huan.tv.smartsms.R;

public class AutoSearchAdapter extends CursorAdapter {
	
	
	public AutoSearchAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return View.inflate(context, R.layout.adapter_autosearch, null);
	}
	
	private ViewHolder holder;
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		holder = getViewHolder(view);
		String name = cursor.getString(cursor.getColumnIndex("display_name"));
		String number = cursor.getString(cursor.getColumnIndex("data1"));
		holder.tv_name.setText(name);
		holder.tv_number.setText(number);
	}
	
	@Override
	public CharSequence convertToString(Cursor cursor) {
		String number = cursor.getString(cursor.getColumnIndex("data1"));
		return number;
	}
	
	class ViewHolder{
		private TextView tv_name,tv_number;
		public ViewHolder(View convertView){
			tv_name = (TextView) convertView.findViewById(R.id.tv_name_autocomplete);
			tv_number = (TextView) convertView.findViewById(R.id.tv_number_autocomplete);
		}
	}
	public ViewHolder getViewHolder(View convertView){
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if(holder==null){
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		return holder;
	}

}
