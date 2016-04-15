package com.huan.tv.smartsms.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.base.BaseDialog;
import com.huan.tv.smartsms.utils.UIUtil;
/**
 * 创建分组的对话框
 * @author MrTaoge
 *
 */
public class CreateGroupDialog extends BaseDialog {
	private EditText et_group_name;
	private TextView tv_title;
	private Button btn_confirm,btn_cancel;
	private OnCreateGroupCompleteListener listener;
	private String title;
	public CreateGroupDialog(Context context,String title,OnCreateGroupCompleteListener listener) {
		super(context);
		this.title = title;
		this.listener = listener;
	}

	@Override
	public void initView() {
		setContentView(R.layout.dialog_create_group);
		et_group_name = (EditText) findViewById(R.id.et_group_name);
		btn_confirm = (Button) findViewById(R.id.btn_confirm_group);
		btn_cancel = (Button) findViewById(R.id.btn_cancel_group);
		tv_title = (TextView) findViewById(R.id.tv_title_dialog_group);
	}

	@Override
	public void initData() {
		tv_title.setText(title);
	}

	@Override
	public void setListener() {
		btn_cancel.setOnClickListener(this);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void subClick(View view) {
		switch (view.getId()) {
		case R.id.btn_confirm_group:
			String name = et_group_name.getText().toString();
			if(TextUtils.isEmpty(name)){
				UIUtil.showToast("请输入分组名");
			}else{
				if(listener!=null){
					listener.onConfirm(name);
				}
			}
			break;
		case R.id.btn_cancel_group:
			if(listener!=null){
				listener.onCancel();
			}
			break;

		default:
			break;
		}
		dismiss();
	}
	
	public interface OnCreateGroupCompleteListener{
		void onConfirm(String groupName);
		void onCancel();
	}

}
