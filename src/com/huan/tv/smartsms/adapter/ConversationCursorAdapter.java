package com.huan.tv.smartsms.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.huan.tv.smartsms.R;
import com.huan.tv.smartsms.dao.ContactDao;
import com.huan.tv.smartsms.ui.fragment.ConversationFragment;
import com.huan.tv.smartsms.utils.LogUtil;
import com.huan.tv.smartsms.utils.UIUtil;
/**
 * 
 * @author MrTaoge
 *
 */
public class ConversationCursorAdapter extends CursorAdapter {
	
	private static int INDEX_CONVERSATION_BODY = 0;
	private static int INDEX_CONVERSATION_THREAD_ID = 1;
	private static int INDEX_CONVERSATION_COUNT = 2;
	private static int INDEX_CONVERSATION_ADDRESS = 3;
	private static int INDEX_CONVERSATION_DATE = 4;
	/**短信条目左边的checkbook是否被选中*/
	private boolean isSelected = false;
	private ArrayList<Integer> conversationIds = new ArrayList<Integer>();

	public ConversationCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	/**直接从xml加载布局文件，adapter的getView方法会调用到这个方法。*/
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = View.inflate(context, R.layout.adapter_conversation, null);
		return view;
	}
	
	private ViewHolder holder;
	/**直接给控件填充数据*/
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		holder = getViewHolder(view);
		String body = cursor.getString(INDEX_CONVERSATION_BODY);
		long date = cursor.getLong(INDEX_CONVERSATION_DATE);
		int count = cursor.getInt(INDEX_CONVERSATION_COUNT);
		String originalAddress = cursor.getString(INDEX_CONVERSATION_ADDRESS);
		String address = UIUtil.formatPhoneNumber(originalAddress);
		holder.tv_body.setText(body);
		if(DateUtils.isToday(date)){
			//如果是今天的时间，那么就显示时间。
			holder.tv_time.setText(DateFormat.getTimeFormat(context).format(date));
		}else{
			//不是今天的时间的话就显示日期。
			holder.tv_time.setText(DateFormat.getDateFormat(context).format(date));
		}
//		String name = ContactDao.getContactNameByAddress(context.getContentResolver(), address);//用换缓存数据代替反复查数据库的操作，优化内存。
		String name = "";
		if(ConversationFragment.numberIdMap.containsKey(address)){//这步是必须要加的判断。因为这个装联系人id的map封装的值都是查询联系人表而来的，也就是通讯录有的联系人才有id加入到该集合。但是我们发送的短息，接收方并不一定都在通讯录里，这时就不会查到它的联系人id，此时用id去查这个号的头像时显然会报空指针，而且通讯录外的陌生人根本就不可能 有头像。
			name = ConversationFragment.numberNameMap.get(address.startsWith("+86")?address.substring(3):address);
		}
		if(TextUtils.isEmpty(name)){
			holder.tv_contact.setText(address+"("+count+")");
		}else{
			holder.tv_contact.setText(name+"("+count+")");
		}
		Bitmap avatar = ContactDao.getContactAvatarByAddress(context.getContentResolver(), address);
//		Bitmap avatar = ContactDao.getContactAvatarByCacheId(context.getContentResolver(), address);
		if(avatar!=null){
			holder.iv_avatar.setBackground(new BitmapDrawable(avatar));
		}else{
			holder.iv_avatar.setBackgroundResource(R.drawable.img_default_avatar);
		}
		
		if(isSelected){
			holder.checkBox.setVisibility(View.VISIBLE);
		}else{
			holder.checkBox.setVisibility(View.GONE);
		}
		if(isSelected){
			holder.checkBox.setChecked(conversationIds.contains(cursor.getInt(INDEX_CONVERSATION_THREAD_ID)));
		}
	}
	
	class ViewHolder{
		private ImageView iv_avatar;
		private TextView tv_contact,tv_body,tv_time;
		private CheckBox checkBox;
		public ViewHolder(View convertView){
			iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar_conversation);
			tv_body = (TextView) convertView.findViewById(R.id.tv_body_conversation);
			tv_contact = (TextView) convertView.findViewById(R.id.tv_contact_conversation);
			tv_time = (TextView) convertView.findViewById(R.id.tv_time_conversation);
			checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_conversation);
		}
	}
	public ViewHolder getViewHolder(View convertView){
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if(holder==null){
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		return holder;
	}
	
	public void setSelectedState(boolean isSelected){
		this.isSelected = isSelected;
		if(!isSelected){
			conversationIds.clear();
			notifyDataSetChanged();
		}
	}
	public boolean getSelectedState(){
		return isSelected;
	}
	/**选中全部条目*/
	public void selectAll(){
		Cursor cursor = getCursor();
		cursor.moveToPosition(-1);//先将指针移动到最开始的位置，防止从中间遍历，造成数据漏掉。
		while(cursor.moveToNext()){
			int threadId = cursor.getInt(INDEX_CONVERSATION_THREAD_ID);
			conversationIds.add(threadId);
		}
		notifyDataSetChanged();
	}
	/**
	 * 选中单个条目
	 * @param position
	 */
	public void setSingleItem(int position){
		Cursor cursor = (Cursor) getItem(position);
		conversationIds.add(cursor.getInt(INDEX_CONVERSATION_THREAD_ID));
		notifyDataSetChanged();
	}
	public ArrayList<Integer> getConversationIds(){
		return conversationIds;
	}
}
