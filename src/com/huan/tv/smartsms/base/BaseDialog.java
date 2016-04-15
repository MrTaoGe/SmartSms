package com.huan.tv.smartsms.base;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.huan.tv.smartsms.R;
/**
 * 自定义对话框的基类。
 * <p>用到了自定义主题</p>
 * @author MrTaoge
 *
 */
public abstract class BaseDialog extends AlertDialog implements OnClickListener{
	public BaseDialog(Context context) {
		super(context, R.style.BaseDialog);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		setListener();
	}
	
	public abstract void initView();
	public abstract void initData();
	public abstract void setListener();
	/**子类重写的点击事件*/
	public abstract void subClick(View view);
	
	public void onClick(View view) {
		subClick(view);
	}
}
