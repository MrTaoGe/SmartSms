package com.huan.tv.smartsms.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

public abstract class BaseActivity extends FragmentActivity implements OnClickListener{
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
