package com.huan.tv.smartsms.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.TextView;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.adapter.AutoSearchAdapter;
import com.huan.tv.smartsms.base.BaseActivity;
import com.huan.tv.smartsms.dao.ContactDao;
import com.huan.tv.smartsms.dao.SmsDao;
import com.huan.tv.smartsms.utils.UIUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
/**
 * 创建新短信界面。
 * @author MrTaoge
 *
 */
public class NewSmsActivity extends BaseActivity {
	
	@ViewInject(R.id.et_chose_contact)
	private AutoCompleteTextView et_chose_contact;
	@ViewInject(R.id.et_input_content)
	private EditText et_input_content;
	@ViewInject(R.id.btn_back)
	private Button btn_back;
	@ViewInject(R.id.btn_send_new)
	private Button btn_send;
	@ViewInject(R.id.tv_title_bar)
	private TextView tv_titlebar;
	@ViewInject(R.id.btn_chose_contact)
	private Button btn_chose_contact;
	
//	private SimpleAsyncQueryHandler asyncQueryHandler;
	private AutoSearchAdapter autoSearchAdapter;
	private String[] projections;

	@Override
	public void initView() {
		setContentView(R.layout.activity_new_sms);
		ViewUtils.inject(this);
		initTitleBar();
	}

	private void initTitleBar() {
		tv_titlebar.setText("发送短信");
	}

	@Override
	public void initData() {
//		asyncQueryHandler = new SimpleAsyncQueryHandler(getContentResolver());
		projections = new String[]{"_id","data1","display_name"};
		
		if(autoSearchAdapter==null){
			autoSearchAdapter = new AutoSearchAdapter(this, null);
			et_chose_contact.setAdapter(autoSearchAdapter);
		}else{
			autoSearchAdapter.notifyDataSetChanged();
		}
		autoSearchAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			
			public Cursor runQuery(CharSequence constraint) {
				String selection = "data1 like '"+et_chose_contact.getText().toString()+"%'";//每次查询都会执行这个回调方法，所以这个模糊查询的条件不应该写在外边。
				Cursor cursor = getContentResolver().query(Phone.CONTENT_URI, projections, selection, null, null);
				return cursor;
			}
		});
		et_chose_contact.setDropDownBackgroundResource(R.drawable.bg_btn_normal);
		et_chose_contact.setDropDownVerticalOffset(5);
	}

	@Override
	public void setListener() {
		btn_back.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_chose_contact.setOnClickListener(this);
	}

	@Override
	public void subClick(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_send_new:
			String address = et_chose_contact.getText().toString();
			if(TextUtils.isEmpty(address)){
				UIUtil.showToast("请输入短信接收方");
				return;
			}
			String content = et_input_content.getText().toString();
			
			if(TextUtils.isEmpty(content)){
				UIUtil.showToast("短信内容不能为空");
				return;
			}else{
				SmsDao.sendSms(this, address, content);
			}
			break;
		case R.id.btn_chose_contact:
			Intent intent = new Intent("android.intent.action.PICK");
			intent.setType("vnd.android.cursor.dir/contact");
			startActivityForResult(intent,0);
			
			break;
		default:
			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(intent!=null&&intent.getData()!=null){
			Uri uri = intent.getData();
			String[] projection = new String[]{"_id","has_phone_number"};
			Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
			if(cursor!=null&&cursor.moveToNext()){
				int contactId = cursor.getInt(0);
				int has_phone_number = cursor.getInt(1);
				if(has_phone_number==1){
					String number = ContactDao.getNumberById(getContentResolver(),contactId);
					et_chose_contact.setText(number);
					et_input_content.requestFocus();
				}else{
					UIUtil.showToast("该联系人没有设置电话号码");
				}
				cursor.close();
			}
		}
	}

}
