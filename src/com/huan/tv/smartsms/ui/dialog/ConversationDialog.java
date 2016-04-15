package com.huan.tv.smartsms.ui.dialog;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.base.BaseDialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 对话界面用到的自定义对话框
 * @author MrTaoge
 */
public class ConversationDialog extends BaseDialog {
	private Context context;
	private OnConversationDialogClickListener listener;
	private Button btn_confirm,btn_cancel;
	private TextView tv_notice,tv_content;
	private String notice,content;
	public ConversationDialog(Context context,String notice,String content,OnConversationDialogClickListener listener) {
		super(context);
		this.context = context;
		this.notice  = notice;
		this.content = content;
		this.listener = listener;
	}

	@Override
	public void initView() {
		setContentView(R.layout.dialog_conversation);
		tv_content = (TextView) findViewById(R.id.tv_content_dialog);
		tv_notice = (TextView) findViewById(R.id.tv_notice_dialog);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
	}

	@Override
	public void initData() {
		tv_content.setText(content);
		tv_notice.setText(notice);
	}

	@Override
	public void setListener() {
		btn_cancel.setOnClickListener(this);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void subClick(View view) {
		switch (view.getId()) {
		case R.id.btn_confirm:
			if(listener!=null){
				listener.onConfirm();
			}
			break;
		case R.id.btn_cancel:
			if(listener!=null){
				listener.onCancel();
			}
			break;

		default:
			break;
		}
		dismiss();
	}
	
	public interface OnConversationDialogClickListener{
		void onConfirm();
		void onCancel();
	}
}
