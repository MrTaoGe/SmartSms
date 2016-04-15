package com.huan.tv.smartsms.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.adapter.ConversationCursorAdapter;
import com.huan.tv.smartsms.base.BaseActivity;
import com.huan.tv.smartsms.dao.SimpleAsyncQueryHandler;
import com.huan.tv.smartsms.global.Constant;
import com.huan.tv.smartsms.utils.LogUtil;

public class GroupDetailActivity extends BaseActivity {
	private ListView listview;
	private TextView tv_title;
	private Button btn_back;
	private int groupId;
	private ConversationCursorAdapter adapter;
	
	@Override
	public void initView() {
		setContentView(R.layout.activity_group_detail);
		listview = (ListView) findViewById(R.id.listview_group_detail);
		tv_title = (TextView) findViewById(R.id.tv_title_bar);
		btn_back = (Button) findViewById(R.id.btn_back);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		if(intent!=null){
			String title = intent.getStringExtra("groupName");
			tv_title.setText(title);
			groupId = intent.getIntExtra("groupId", -1);
		}
		
		if(adapter==null){
			adapter = new ConversationCursorAdapter(this, null);
			listview.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
		
		String selection = buildQueryConditionByGroupId(groupId);
		loadConversations(selection);
	}
	
	private void loadConversations(String selection) {
		String[] projection = new String[]{
				"sms.body AS snippet",
				"sms.thread_id AS _id",
				"groups.msg_count AS msg_count",
				"address AS address",
				"date AS date"
				};
		String orderBy = "date DESC";
		SimpleAsyncQueryHandler asyncQueryHandler = new SimpleAsyncQueryHandler(getContentResolver());
		asyncQueryHandler.startQuery(0, adapter, Constant.URI.uri_conversation, projection, selection, null, orderBy);
	}

	/**
	 * 通过groupId构建查询相应分组下所有会话短信的条件。
	 * @param groupId
	 */
	private String buildQueryConditionByGroupId(int groupId) {
		String selection = " thread_id in (";
		Cursor cursor = getContentResolver().query(Constant.URI.URI_THREAD_GROUP_QUERY, new String[]{"thread_id"}, "group_id="+groupId, null, null);
		if(cursor!=null){
			while(cursor.moveToNext()){
				selection += cursor.getInt(0);
				if(cursor.isLast()){
					selection += ")";
				}else{
					selection += ",";
				}
			}
		}
		return selection;
	}

	@Override
	public void setListener() {
		btn_back.setOnClickListener(this);
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Cursor cursor = (Cursor) adapter.getItem(position);
				int thread_id = cursor.getInt(1);
				String address = cursor.getString(3);
				Intent intent = new Intent(GroupDetailActivity.this,SMSDetailActivity.class);
				intent.putExtra("address", address);
				intent.putExtra("thread_id", thread_id);
				startActivity(intent);
			}
		});
	}

	@Override
	public void subClick(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			
			break;

		default:
			break;
		}
	}

}
