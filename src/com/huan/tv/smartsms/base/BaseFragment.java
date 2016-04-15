package com.huan.tv.smartsms.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.huan.tv.smartsms.utils.LogUtil;
import com.lidroid.xutils.ViewUtils;

public abstract class BaseFragment extends Fragment implements OnClickListener{
	
	protected View view;
	/**fragment所依附的activity*/
	protected Activity activityAttched;
	/**是否已经加载过数据*/
	private boolean hasLoadData = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		if(view==null){
			view = initView(inflater, container, savedInstanceState);
		}
		ViewUtils.inject(this,view);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		activityAttched = getActivity();
		if(!hasLoadData){//避免重复加载相同的数据，优化一下程序。但是如果存在fragment不能刷新的问题，那么也应该从这里或者onCreate等考虑一下。
			initData();
			hasLoadData = true;
		}
		setListener();
	}
	
	public abstract View initView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState);
	public abstract void initData();
	public abstract void setListener();
	/**子类重写的点击事件*/
	public abstract void subClick(View view);
	
	public void onClick(View view) {
		subClick(view);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(view!=null){
			((ViewGroup)view.getParent()).removeView(view);
		}
	}
	@Override
	public void onDetach() {
		super.onDetach();
		if(view!=null){
			view = null;//fragment和activity解绑时如果fragment的view还没有消除，那么在这里清除，优化一下。这步不能在onDestroy中做，因为此时还没有消除fragment，会造成切换fragment时没有页面的情况。
		}
	}
}
