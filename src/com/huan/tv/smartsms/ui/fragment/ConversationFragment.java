package com.huan.tv.smartsms.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.adapter.ConversationCursorAdapter;
import com.huan.tv.smartsms.base.BaseFragment;
import com.huan.tv.smartsms.dao.GroupsDao;
import com.huan.tv.smartsms.dao.SimpleAsyncQueryHandler;
import com.huan.tv.smartsms.dao.ThreadGroupDao;
import com.huan.tv.smartsms.global.Constant;
import com.huan.tv.smartsms.ui.activity.NewSmsActivity;
import com.huan.tv.smartsms.ui.activity.SMSDetailActivity;
import com.huan.tv.smartsms.ui.dialog.ConversationDialog.OnConversationDialogClickListener;
import com.huan.tv.smartsms.ui.dialog.DeleteSMSDialog;
import com.huan.tv.smartsms.ui.dialog.DeleteSMSDialog.OnCancelDeleteListener;
import com.huan.tv.smartsms.ui.dialog.HandleGroupDialog.OnHandleGroupDialogClickListener;
import com.huan.tv.smartsms.utils.LogUtil;
import com.huan.tv.smartsms.utils.UIUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class ConversationFragment extends BaseFragment {
	private static final int DELETE_FINISH = 0;
	private static final int DELETING = 1;
	
	@ViewInject(R.id.ll_edit_conversation)
	private LinearLayout ll_edit;
	@ViewInject(R.id.ll_select_conversation)
	private LinearLayout ll_select;
	@ViewInject(R.id.btn_edit)
	private Button btn_edit;
	@ViewInject(R.id.btn_create_sms)
	private Button btn_create_sms;
	@ViewInject(R.id.btn_select_all)
	private Button btn_select_all;
	@ViewInject(R.id.btn_cancel_select)
	private Button btn_cancel_select;
	@ViewInject(R.id.btn_delete)
	private Button btn_delete;
	@ViewInject(R.id.listview_conversation)
	private ListView listView;
	
	private SimpleAsyncQueryHandler asyncQueryHandler;
	private ConversationCursorAdapter cursorAdapter;
	private ArrayList<Integer> conversationIdsSelected;
	private int maxProgress;
	/**是否取消删除短信*/
	private boolean isCancelDeleteSms = false;
	private DeleteSMSDialog deleteSMSDialog;
	
	public static HashMap<String, String> numberNameMap;
	public static HashMap<String, Integer> numberIdMap;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DELETE_FINISH:
				switchMenuBar(Constant.MENU_BAR_TO_UP);
				cursorAdapter.setSelectedState(false);
				cursorAdapter.notifyDataSetChanged();
				deleteSMSDialog.dismiss();
				isCancelDeleteSms = false;//删除完毕后更新标记。
				break;
			case DELETING:
				deleteSMSDialog.updateProgressBar(msg.arg1);
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	public View initView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_conversation, null);
		
		return view;
	}

	@Override
	public void initData() {
		numberNameMap = new HashMap<String, String>();
		numberIdMap = new HashMap<String, Integer>();
		
		initContactsCache();
		registerContactInfoChangeListener();
		
		cursorAdapter = new ConversationCursorAdapter(activityAttched, null);
		listView.setAdapter(cursorAdapter);
		
		asyncQueryHandler = new SimpleAsyncQueryHandler(activityAttched.getContentResolver());
		String[] projection = new String[]{
				"sms.body AS snippet",
				"sms.thread_id AS _id",
				"groups.msg_count AS msg_count",
				"address AS address",
				"date AS date"
				};
		String orderBy = "date DESC";
		asyncQueryHandler.startQuery(0, cursorAdapter, Constant.URI.uri_conversation, projection, null, null,orderBy);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(cursorAdapter.getSelectedState()){//选中状态的前提下点击。
					cursorAdapter.setSingleItem(position);
				}else{//未选中，直接进入对话界面。
					Cursor cursor = (Cursor) cursorAdapter.getItem(position);
					int thread_id = cursor.getInt(1);
					String address = cursor.getString(3);
					Intent intent = new Intent(activityAttched,SMSDetailActivity.class);
					intent.putExtra("address", address);
					intent.putExtra("thread_id", thread_id);
					startActivity(intent);
				}
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
				Cursor cursor = (Cursor) cursorAdapter.getItem(position);
				int threadId = -1;
				if(cursor!=null){
					threadId = cursor.getInt(1);
				}
				if(ThreadGroupDao.isInGroup(activityAttched.getContentResolver(), threadId)){
					//提示是否要从该分组移除。
					LogUtil.i(this, "已经有分组");
					removeFromTheGroup(threadId);
				}else{
					//将当前对话添加到某个分组。
					//先判断是否创建过某些分组。
					if(GroupsDao.hasCreatedSomeGroups(activityAttched.getContentResolver())){
						addToTheGroup(threadId);
					}else{
						UIUtil.showToast("您还没有创建分组");
					}
				}
				return true;
			}
		});
	}
	/**
	 * 缓存联系人信息，避免大量重复的对数据库进行操作，优化程序。
	 */
	private void initContactsCache() {
		String[] projection = new String[]{"contact_id","data1","display_name"};
		AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(activityAttched.getContentResolver()) {
			@Override
			protected void onQueryComplete(int token, Object cookie,
					Cursor cursor) {
				super.onQueryComplete(token, cookie, cursor);
				if(cursor!=null){
					while(cursor.moveToNext()){
						String numberName = UIUtil.formatPhoneNumber(cursor.getString(1));
						String contactName = cursor.getString(2);
						int numberId = cursor.getInt(0);
						numberNameMap.put(numberName, contactName);
						numberIdMap.put(numberName, numberId);
						LogUtil.i("numberName", numberName);
					}
					cursor.close();
				}
				cursorAdapter.notifyDataSetChanged();
			}
		};
		//异步查询数据库。
		asyncQueryHandler.startQuery(0, 0, Phone.CONTENT_URI, projection, null, null, null);
	}

	/**
	 * 将当前对话从分组中移除。
	 * @param threadId 选中的对话在表中的对应id。
	 */
	protected void removeFromTheGroup(final int threadId) {
		String groupName = "";
		final int groupId = ThreadGroupDao.getGroupIdByThreadId(activityAttched.getContentResolver(), threadId);
		groupName = GroupsDao.getGroupNameByGroupId(activityAttched.getContentResolver(), groupId);
		UIUtil.showConversationDialog(activityAttched, "通知", "当前会话已经在("+groupName+")分组中，确定要将其从当前分组中删除吗？", new OnConversationDialogClickListener() {
			
			public void onConfirm() {
				int deleteRows = ThreadGroupDao.deleteConversationFromGroupById(activityAttched.getContentResolver(), threadId,groupId);
				if(deleteRows>0){
					UIUtil.showToast("当前会话已经从分组中移除");
				}
			}
			
			public void onCancel() {
			}
		});
	}
	/**
	 * 将当前对话添加到指定分组
	 * @param threadId 选中的对话在表中的对应id。
	 */
	protected void addToTheGroup(final int threadId) {
		final Cursor cursor = activityAttched.getContentResolver().query(Constant.URI.URI_GROUP_QUERY, new String[]{"_id","name"},null,null,null);
		
		if(cursor!=null&&cursor.getCount()>0){
			String[] groupNames = new String[cursor.getCount()];
			while(cursor.moveToNext()){
				groupNames[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex("name"));
			}
			UIUtil.showHandleGroupDialog(activityAttched, "选择分组", groupNames, new OnHandleGroupDialogClickListener() {
				
				public void onGroupDialogClick(int position) {
					cursor.moveToPosition(position);//将指针移到选中的分组上。
					int groupId = cursor.getInt(0);//通过groupId将当前会话插入到groups表中。
					ThreadGroupDao.insertConversataion2GroupById(activityAttched.getContentResolver(), threadId, groupId);
				}
			});
			
		}
	}
	/**注册联系人信息改变的监听*/
	public void registerContactInfoChangeListener(){
		activityAttched.getContentResolver().registerContentObserver(Phone.CONTENT_URI, true, new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				if(selfChange){
					initContactsCache();
				}
			}
		});
	}

	@Override
	public void setListener() {
		btn_edit.setOnClickListener(this);
		btn_create_sms.setOnClickListener(this);
		btn_select_all.setOnClickListener(this);
		btn_cancel_select.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
	}

	@Override
	public void subClick(View view) {
		switch (view.getId()) {
		case R.id.btn_edit:
			switchMenuBar(Constant.MENU_BAR_TO_DOWN);
			cursorAdapter.setSelectedState(true);
			cursorAdapter.notifyDataSetChanged();
			break;
		case R.id.btn_create_sms:
			Intent intent = new Intent(activityAttched,NewSmsActivity.class);
			activityAttched.startActivity(intent);
			break;
		case R.id.btn_select_all:
			cursorAdapter.selectAll();
			break;
		case R.id.btn_cancel_select:
			switchMenuBar(Constant.MENU_BAR_TO_UP);
			cursorAdapter.setSelectedState(false);
			cursorAdapter.notifyDataSetChanged();
			break;
		case R.id.btn_delete:
			UIUtil.showConversationDialog(activityAttched,"通知","您确定要删除当前的短信吗？",new OnConversationDialogClickListener() {
				public void onConfirm() {
					deleteSms();
				}
				
				public void onCancel() {
				}
			});
			break;

		default:
			break;
		}
	}
	
	/**删除短信*/
	protected void deleteSms() {
		conversationIdsSelected = cursorAdapter.getConversationIds();
		maxProgress = conversationIdsSelected.size();
		deleteSMSDialog = UIUtil.showDeleteDialog(activityAttched,maxProgress,new OnCancelDeleteListener() {
			
			public void onCancelDelete() {
				isCancelDeleteSms = true;
			}
		});
		//查询数据库，然后删除相应的短信属于耗时操作，应该异步进行。
		new Thread(){
			public void run() {
				int size = conversationIdsSelected.size();
				for(int i=0; i<size; i++){
					SystemClock.sleep(1000);//给个缓冲，让用户有取消删除的机会
					if(isCancelDeleteSms)break;//取消删除短信
					int thread_id = conversationIdsSelected.get(i);
					String where = "thread_id = "+thread_id;
					//删除短信也是用内容提供者自身处理。
					activityAttched.getContentResolver().delete(Constant.URI.uri_sms, where, null);
					Message msg = handler.obtainMessage();
					msg.what = DELETING;
					msg.arg1 = i+1;//将删除短信的进度传出去便于刷新进度条
					handler.sendMessage(msg);
				}
				cursorAdapter.getConversationIds().clear();
				handler.sendEmptyMessage(DELETE_FINISH);
			};
		}.start();
		
	}

	/**
	 * 切换底部菜单栏
	 * @param direction 菜单栏的移动方向。
	 */
	private void switchMenuBar(int direction) {
		if(direction==0){
			ViewPropertyAnimator.animate(ll_edit).translationY(getResources().getDimension(R.dimen.dimen_60dip)).setDuration(200);
			new Handler().postDelayed(new Runnable() {
				
				public void run() {
					ViewPropertyAnimator.animate(ll_select).translationY(0).setDuration(200);
				}
			}, 200);
		}else if(direction==1){
			ViewPropertyAnimator.animate(ll_select).translationY(getResources().getDimension(R.dimen.dimen_60dip)).setDuration(200);
			new Handler().postDelayed(new Runnable() {
				
				public void run() {
					ViewPropertyAnimator.animate(ll_edit).translationY(0).setDuration(200);
				}
			},200);
			
		}else{
			return;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		numberIdMap.clear();
		numberNameMap.clear();
	}
	
}
