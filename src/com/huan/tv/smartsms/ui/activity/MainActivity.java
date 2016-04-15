package com.huan.tv.smartsms.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.LinearGradient;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.adapter.MainPageAdapter;
import com.huan.tv.smartsms.base.BaseActivity;
import com.huan.tv.smartsms.ui.fragment.ConversationFragment;
import com.huan.tv.smartsms.ui.fragment.GroupFragment;
import com.huan.tv.smartsms.ui.fragment.SearchFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class MainActivity extends BaseActivity {
	@ViewInject(R.id.viewpager_main)
	private ViewPager viewpager;
	@ViewInject(R.id.tv_conversation)
	private TextView tv_conversation;
	@ViewInject(R.id.tv_group)
	private TextView tv_group;
	@ViewInject(R.id.tv_search)
	private TextView tv_search;
	@ViewInject(R.id.indicator)
	private View indictor;
	@ViewInject(R.id.ll_conversation)
	private LinearLayout ll_conversation;
	@ViewInject(R.id.ll_group)
	private LinearLayout ll_group;
	@ViewInject(R.id.ll_search)
	private LinearLayout ll_search;
	
	private List<Fragment> fragmentList;
	private MainPageAdapter mainPageAdapter;
	/**标题栏中的指针宽度*/
	private int indictorWidth;
	/**指针的移动距离*/
	private int indictorMoveDis;
	
	@Override
	public void initView() {
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
	}

	@Override
	public void initData() {
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new ConversationFragment());
		fragmentList.add(new GroupFragment());
		fragmentList.add(new SearchFragment());
		
		initIndicatorLineWidth();
		hightLightTitle(0);
		
		if(mainPageAdapter==null){
			mainPageAdapter = new MainPageAdapter(getSupportFragmentManager(), fragmentList);
			viewpager.setAdapter(mainPageAdapter);
		}else{
			mainPageAdapter.notifyDataSetChanged();
		}
		
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			
			public void onPageSelected(int position) {
				hightLightTitle(position);
			}
			
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				indictorMoveDis = positionOffsetPixels/3+indictorWidth*position;
				ViewPropertyAnimator.animate(indictor).translationX(indictorMoveDis).setDuration(0);
			}
			
			public void onPageScrollStateChanged(int state) {
				InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		
	}
	/**当前被选中的页面的标题高亮突出*/
	protected void hightLightTitle(int position) {
		tv_conversation.setTextColor(position==0?getResources().getColor(R.color.tv_title_main_focus):getResources().getColor(R.color.tv_title_main_unfocus));
		tv_group.setTextColor(position==1?getResources().getColor(R.color.tv_title_main_focus):getResources().getColor(R.color.tv_title_main_unfocus));
		tv_search.setTextColor(position==2?getResources().getColor(R.color.tv_title_main_focus):getResources().getColor(R.color.tv_title_main_unfocus));
		
		ViewPropertyAnimator.animate(tv_conversation).scaleX(position==0?1.2f:1.0f).setDuration(200);
		ViewPropertyAnimator.animate(tv_conversation).scaleY(position==0?1.2f:1.0f).setDuration(200);
		
		ViewPropertyAnimator.animate(tv_group).scaleX(position==1?1.2f:1.0f).setDuration(200);
		ViewPropertyAnimator.animate(tv_group).scaleY(position==1?1.2f:1.0f).setDuration(200);
		
		ViewPropertyAnimator.animate(tv_search).scaleX(position==2?1.2f:1.0f).setDuration(200);
		ViewPropertyAnimator.animate(tv_search).scaleY(position==2?1.2f:1.0f).setDuration(200);
			
	}

	/**计算标题栏处指针的宽度*/
	private void initIndicatorLineWidth() {
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		indictorWidth = screenWidth/fragmentList.size();
		indictor.getLayoutParams().width = indictorWidth;
		indictor.requestLayout();
	}

	@Override
	public void setListener() {
		ll_conversation.setOnClickListener(this);
		ll_group.setOnClickListener(this);
		ll_search.setOnClickListener(this);
	}

	@Override
	public void subClick(View view) {
		switch (view.getId()) {
		case R.id.ll_conversation:
			viewpager.setCurrentItem(0);
			break;
		case R.id.ll_group:
			viewpager.setCurrentItem(1);
			break;
		case R.id.ll_search:
			viewpager.setCurrentItem(2);
			break;
		default:
			break;
		}
	}
	
}
