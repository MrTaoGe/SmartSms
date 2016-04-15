package com.huan.tv.smartsms.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.adapter.ConversationCursorAdapter;
import com.huan.tv.smartsms.base.BaseFragment;
import com.huan.tv.smartsms.dao.SimpleAsyncQueryHandler;
import com.huan.tv.smartsms.global.Constant;
import com.huan.tv.smartsms.ui.activity.SMSDetailActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class SearchFragment extends BaseFragment {
	@ViewInject(R.id.listview_search)
	private ListView listview;
	@ViewInject(R.id.et_search)
	private EditText et_search;
	
	private ConversationCursorAdapter adapter;
	private SimpleAsyncQueryHandler asyncQueryHandler;
	
	String[] projection = new String[]{
			"sms.body AS snippet",
			"sms.thread_id AS _id",
			"groups.msg_count AS msg_count",
			"address AS address",
			"date AS date"
			};
	String orderBy = "date DESC";
	
	@Override
	public View initView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_search, null);
		return view;
	}

	@Override
	public void initData() {
		if(adapter==null){
			adapter = new ConversationCursorAdapter(activityAttched, null);
			listview.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
		
		asyncQueryHandler = new SimpleAsyncQueryHandler(activityAttched.getContentResolver());
	}

	@Override
	public void setListener() {
		listview.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				switch (MotionEventCompat.getActionMasked(event)) {
				case MotionEvent.ACTION_DOWN:
					InputMethodManager manager = (InputMethodManager) activityAttched.getSystemService(Context.INPUT_METHOD_SERVICE);
					manager.hideSoftInputFromWindow(et_search.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					break;

				default:
					break;
				}
				return false;
			}
		});
		et_search.addTextChangedListener(new TextWatcher() {//给控件添加监听，除了set...外，还可以考虑add...
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String keyWords = et_search.getText().toString();
				if(TextUtils.isEmpty(keyWords))return;
				String selection = "body like '%"+keyWords+"%'";//模糊查询。
				asyncQueryHandler.startQuery(0, adapter, Constant.URI.uri_conversation, projection, selection, null, orderBy);
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			
			public void afterTextChanged(Editable s) {
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Cursor cursor = (Cursor) adapter.getItem(position);
				int thread_id = cursor.getInt(1);
				String address = cursor.getString(3);
				Intent intent = new Intent(activityAttched,SMSDetailActivity.class);
				intent.putExtra("address", address);
				intent.putExtra("thread_id", thread_id);
				startActivity(intent);
			}
		});
	}

	@Override
	public void subClick(View view) {
		
	}

}
