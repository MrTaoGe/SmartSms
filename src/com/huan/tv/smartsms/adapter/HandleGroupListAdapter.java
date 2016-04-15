package com.huan.tv.smartsms.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huan.tv.smartsms.R;

public class HandleGroupListAdapter extends BaseAdapter {
	private Context context;
	private String[] operations;
	public HandleGroupListAdapter(Context context,String[] operations){
		this.context = context;
		this.operations = operations;
	}
	
	public int getCount() {
		return operations.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
	
	private ViewHolder holder;
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = View.inflate(context, R.layout.adapter_handle_group,null);
		}
		holder = getViewHolder(convertView);
		holder.tv_handle.setText(operations[position]);
		return convertView;
	}
	
	class ViewHolder{
		private TextView tv_handle;
		private ViewHolder(View convertView){
			tv_handle = (TextView) convertView.findViewById(R.id.tv_handle);
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
