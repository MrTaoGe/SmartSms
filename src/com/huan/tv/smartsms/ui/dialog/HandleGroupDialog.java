package com.huan.tv.smartsms.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.adapter.HandleGroupListAdapter;
import com.huan.tv.smartsms.base.BaseDialog;
/**
 * 对群组可以进行重命名和删除操作的对话框。
 * @author MrTaoge
 *
 */
public class HandleGroupDialog extends BaseDialog {

	private ListView listView;
	private TextView tv_title;
	
	private String title;
	private String[] handles;
	private OnHandleGroupDialogClickListener listener;
	private Context context;
	private HandleGroupListAdapter adapter;
	
	public HandleGroupDialog(Context context,String title,String[] handleNames,OnHandleGroupDialogClickListener listener) {
		super(context);
		this.title = title;
		this.handles = handleNames;
		this.listener = listener;
		this.context = context;
	}

	@Override
	public void initView() {
		setContentView(R.layout.dialog_group_list);
		listView = (ListView) findViewById(R.id.listview_handle_group);
		tv_title = (TextView) findViewById(R.id.tv_title_handle_group);
	}

	@Override
	public void initData() {
		tv_title.setText(title);
		if(adapter==null){
			adapter = new HandleGroupListAdapter(context, handles);
			listView.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void setListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(listener!=null){
					listener.onGroupDialogClick(position);
				}
				dismiss();
			}
		});
	}

	@Override
	public void subClick(View view) {

	}
	
	public interface OnHandleGroupDialogClickListener{
		void onGroupDialogClick(int position);
	}
}
