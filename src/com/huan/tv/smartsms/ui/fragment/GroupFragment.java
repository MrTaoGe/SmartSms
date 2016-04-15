package com.huan.tv.smartsms.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.adapter.GroupCursorAdapter;
import com.huan.tv.smartsms.base.BaseFragment;
import com.huan.tv.smartsms.dao.GroupsDao;
import com.huan.tv.smartsms.dao.SimpleAsyncQueryHandler;
import com.huan.tv.smartsms.global.Constant;
import com.huan.tv.smartsms.ui.activity.GroupDetailActivity;
import com.huan.tv.smartsms.ui.dialog.ConversationDialog.OnConversationDialogClickListener;
import com.huan.tv.smartsms.ui.dialog.CreateGroupDialog.OnCreateGroupCompleteListener;
import com.huan.tv.smartsms.ui.dialog.HandleGroupDialog.OnHandleGroupDialogClickListener;
import com.huan.tv.smartsms.utils.UIUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

public class GroupFragment extends BaseFragment {
	@ViewInject(R.id.btn_create_group)
	private Button btn_create_group;
	@ViewInject(R.id.listview_group)
	private ListView listview;
	
	private SimpleAsyncQueryHandler asyncQueryHandler;
	private GroupCursorAdapter adapter;
	
	@Override
	public View initView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_group, null);
		return view;
	}

	@Override
	public void initData() {
		asyncQueryHandler = new SimpleAsyncQueryHandler(activityAttched.getContentResolver());
		if(adapter==null){
			adapter = new GroupCursorAdapter(activityAttched, null);
			listview.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
		asyncQueryHandler.startQuery(0, adapter, Constant.URI.URI_GROUP_QUERY, null, null, null, "createdate DESC");
	}

	@Override
	public void setListener() {
		btn_create_group.setOnClickListener(this);
		//长按单个条目以后弹出对话框，允许用户对群组进行重命名或者删除。
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
				UIUtil.showHandleGroupDialog(activityAttched, "请选择您要进行的操作", new String[]{"重命名","删除"}, new OnHandleGroupDialogClickListener() {
					
					public void onGroupDialogClick(int which) {
						Cursor cursor = (Cursor) adapter.getItem(position);
						int groupId = -1;
						if(cursor!=null){
							groupId = cursor.getInt(cursor.getColumnIndex("_id"));
//							cursor.close();//这里不能关闭cursor，重命名等操作完成以后会刷新界面，还有自动查询等操作，这里关闭cursor的话，导致页面没有数据。
						}
						switch (which) {
						case 0:
							rename4Group(groupId);
							break;
						case 1:
							deleteGroup(groupId);
							break;

						default:
							break;
						}
					}
				});
				return true;
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Cursor cursor = (Cursor) adapter.getItem(position);
				int groupId = cursor.getInt(cursor.getColumnIndex("_id"));
				int threadCount = GroupsDao.getThreadCountByGroupId(activityAttched.getContentResolver(), groupId);
				if(threadCount>0){
					String groupName = GroupsDao.getGroupNameByGroupId(activityAttched.getContentResolver(), groupId);
					Intent intent = new Intent(activityAttched,GroupDetailActivity.class);
					intent.putExtra("groupName", groupName);
					intent.putExtra("groupId", groupId);
					activityAttched.startActivity(intent);
				}else{
					UIUtil.showToast("当前分组下没有会话");
				}
			}
		});
	}
	/**
	 * 删除选中的分组。
	 * @param groupId 分组名在群组表——group中对应的id
	 */
	private void deleteGroup(final int groupId) {
		UIUtil.showConversationDialog(activityAttched, "通知", "您确定要删除当前的分组吗", new OnConversationDialogClickListener() {
			
			public void onConfirm() {
				GroupsDao.deleteGroup(activityAttched.getContentResolver(), groupId);
			}
			
			public void onCancel() {
				
			}
		});
	}
	/**
	 * 给选中的群组重命名。
	 * @param groupId groupId 分组名在群组表——group中对应的id
	 */
	private void rename4Group(final int groupId) {
		UIUtil.showCreateGroupDialog(activityAttched, "修改分组名称", new OnCreateGroupCompleteListener() {
			
			public void onConfirm(String groupName) {
				if(!TextUtils.isEmpty(groupName)){
					GroupsDao.renameOfGroup(activityAttched.getContentResolver(), groupId, groupName);
				}else{
					UIUtil.showToast("分组名不能为空");
				}
			}
			
			public void onCancel() {
				
			}
		});
	}
	@Override
	public void subClick(View view) {
		switch (view.getId()) {
		case R.id.btn_create_group:
			UIUtil.showCreateGroupDialog(activityAttched, "创建分组", new OnCreateGroupCompleteListener() {
				
				public void onConfirm(String groupName) {
					GroupsDao.createGroup(activityAttched.getContentResolver(), groupName);
//					LogUtil.i(this, groupName);
				}
				
				public void onCancel() {
					
				}
			});
			break;

		default:
			break;
		}
	}

}
