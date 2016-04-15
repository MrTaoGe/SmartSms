package com.huan.tv.smartsms.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.adapter.SmsDetailAdapter;
import com.huan.tv.smartsms.base.BaseActivity;
import com.huan.tv.smartsms.dao.ContactDao;
import com.huan.tv.smartsms.dao.SimpleAsyncQueryHandler;
import com.huan.tv.smartsms.dao.SmsDao;
import com.huan.tv.smartsms.global.Constant;
import com.huan.tv.smartsms.utils.LogUtil;
import com.huan.tv.smartsms.utils.UIUtil;

public class SMSDetailActivity extends BaseActivity {
	
	private Button btn_back,btn_send;
	private TextView tv_title;
	private EditText et_sms_content;
	private ListView listview;
	private int thread_id;
	private String address;
	private SmsDetailAdapter smsDetailAdapter;
	
	@Override
	public void initView() {
		setContentView(R.layout.activity_sms_detail);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_send = (Button) findViewById(R.id.btn_send_detail);
		et_sms_content = (EditText) findViewById(R.id.et_detail);
		listview = (ListView) findViewById(R.id.listview_detail);
		tv_title = (TextView) findViewById(R.id.tv_title_bar);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		if(intent!=null){
			address = intent.getStringExtra("address");
			thread_id = intent.getIntExtra("thread_id", -1);
			initTitleBar(address);
		}
		if(smsDetailAdapter==null){
			smsDetailAdapter = new SmsDetailAdapter(this, null,listview);
			listview.setAdapter(smsDetailAdapter);
		}else{
			smsDetailAdapter.notifyDataSetChanged();
		}
		listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);//当输入法键盘弹出的时候，整个listview界面不会被覆盖，而是会保持之前的选中状态。
		
		SimpleAsyncQueryHandler queryHandler = new SimpleAsyncQueryHandler(getContentResolver());
		String[] projection = new String[]{"body","date","type","_id"};
		String selection = "thread_id = ?";
		String[] selectionArgs = new String[]{thread_id+""};
		String orderBy = "date";
		queryHandler.startQuery(0, smsDetailAdapter, Constant.URI.uri_sms, projection, selection, selectionArgs, orderBy);
	}

	private void initTitleBar(String address) {
		String name = ContactDao.getContactNameByAddress(getContentResolver(), address);
		if(TextUtils.isEmpty(name)){
			tv_title.setText(address);
		}else{
			tv_title.setText(name);
		}
	}

	@Override
	public void setListener() {
		btn_send.setOnClickListener(this);
		btn_back.setOnClickListener(this);
	}

	@Override
	public void subClick(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_send_detail:
			String content = et_sms_content.getText().toString();
			if(TextUtils.isEmpty(content)){//这个方法包括了字符串对象本身非空的判断以及对象非空的条件下内容长度是否为0的判断，不用再加上content==null的重复判断。
				UIUtil.showToast("短信内容不能为空");
				return;
			}else{
				SmsDao.sendSms(this, address, content);
			}
			et_sms_content.setText("");//发送完毕之后清空et.
			break;

		default:
			break;
		}
	}

}
