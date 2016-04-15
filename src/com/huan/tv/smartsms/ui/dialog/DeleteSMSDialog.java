package com.huan.tv.smartsms.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.base.BaseDialog;

public class DeleteSMSDialog extends BaseDialog {
	private TextView tv_deleting;
	private Button btn_cancel_delete;
	private ProgressBar progressBar;
	private int maxProgress;
	private OnCancelDeleteListener listener;
	
	public DeleteSMSDialog(Context context,int maxProgress,OnCancelDeleteListener listener) {
		super(context);
		this.maxProgress = maxProgress;
		this.listener = listener;
	}

	@Override
	public void initView() {
		setContentView(R.layout.dialog_delete_sms);
		tv_deleting = (TextView) findViewById(R.id.tv_deleting);
		btn_cancel_delete = (Button) findViewById(R.id.btn_cancel_delete);
		progressBar = (ProgressBar) findViewById(R.id.progress_bar_delete);
	}

	@Override
	public void initData() {
		progressBar.setMax(maxProgress);
		tv_deleting.setText("正在删除(0/"+maxProgress+")");
	}

	@Override
	public void setListener() {
		btn_cancel_delete.setOnClickListener(this);
	}
	
	public void updateProgressBar(int progress){
		progressBar.setProgress(progress);
		tv_deleting.setText("正在删除("+progress+"/"+maxProgress+")");
	}

	@Override
	public void subClick(View view) {
		switch (view.getId()) {
		case R.id.btn_cancel_delete:
			if(listener!=null){
				listener.onCancelDelete();
			}
			break;

		default:
			break;
		}
		dismiss();
	}
	public interface OnCancelDeleteListener{
		void onCancelDelete();
	}
}
