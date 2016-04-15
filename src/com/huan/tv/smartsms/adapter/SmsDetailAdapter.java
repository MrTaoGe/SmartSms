package com.huan.tv.smartsms.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.global.Constant;


public class SmsDetailAdapter extends CursorAdapter {
	private final static int DURATION = 1000*60*3;//三分钟
	private Context context;
	private ListView listView;
	public SmsDetailAdapter(Context context, Cursor c,ListView listView) {
		super(context, c);
		this.context = context;
		this.listView = listView;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return View.inflate(context, R.layout.adapter_sms_detail, null);
	}

	private ViewHolder holder;
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		holder = getViewHolder(view);
		String body = cursor.getString(cursor.getColumnIndex("body"));
		long date = cursor.getLong(cursor.getColumnIndex("date"));
		int type = cursor.getInt(cursor.getColumnIndex("type"));
		
		if(type==Constant.SMSTYPE.TYPE_RECEIVE){
			holder.tv_receive.setText(body);
			holder.tv_receive.setVisibility(View.VISIBLE);
			holder.tv_send.setVisibility(View.GONE);
		}else{
			holder.tv_send.setText(body);
			holder.tv_send.setVisibility(View.VISIBLE);
			holder.tv_receive.setVisibility(View.GONE);
		}
		if(cursor.getPosition()>0){
			long lastTime = getLastSmsTime(cursor.getPosition());
			if((date-lastTime)>DURATION){
				holder.tv_time.setVisibility(View.VISIBLE);
				showSmsTime(date);
			}else{
				holder.tv_time.setVisibility(View.GONE);
			}
		}else{
			holder.tv_time.setVisibility(View.VISIBLE);
			showSmsTime(date);
		}
	}
	/**
	 * 获取上一条短信的发送或接收的时间
	 * @param position
	 * @return
	 */
	private long getLastSmsTime(int position) {
		Cursor cursor = (Cursor) getItem(position-1);
		long date = cursor.getLong(cursor.getColumnIndex("date"));
		return date;
	}

	private void showSmsTime(long date) {
		if(DateUtils.isToday(date)){
			holder.tv_time.setText(DateFormat.getTimeFormat(context).format(date));
		}else{
			holder.tv_time.setText(DateFormat.getDateFormat(context).format(date));
		}
	}
	
	
	@Override
	protected void onContentChanged() {
		super.onContentChanged();
		listView.smoothScrollToPosition(getCount());//发送短信以后让短信内容平滑的滚动到上面而不是马上出现，效果生硬。
	}

	//重新这个方法，在这里面将listview中的条目定位到最下面一条。
	@Override
	public void changeCursor(Cursor cursor) {
		super.changeCursor(cursor);
		listView.setSelection(getCount());
	}

	public ViewHolder getViewHolder(View convertView){
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if(holder==null){
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		return holder;
	}
	class ViewHolder{
		private TextView tv_receive,tv_send,tv_time;
		public ViewHolder(View convertView){
			tv_receive = (TextView) convertView.findViewById(R.id.tv_receive);
			tv_send = (TextView) convertView.findViewById(R.id.tv_send);
			tv_time = (TextView) convertView.findViewById(R.id.tv_sms_time);
		}
	}
}
