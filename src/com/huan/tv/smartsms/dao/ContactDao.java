package com.huan.tv.smartsms.dao;

import java.io.InputStream;

import com.huan.tv.smartsms.ui.fragment.ConversationFragment;
import com.huan.tv.smartsms.utils.LogUtil;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;

/**
 * 获取联系人的相关信息。
 * @author MrTaoge
 *
 */
public class ContactDao {
	/**
	 * 根据地址获取联系人的名称
	 * @param contentResolver
	 * @param address
	 * @return
	 */
	public static String getContactNameByAddress(ContentResolver contentResolver,String address){
		String name  = "";
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
		Cursor cursor = contentResolver.query(uri, new String[]{"display_name"}, null, null, null);
		if(cursor!=null&&cursor.moveToNext()){
			name = cursor.getString(0);
			cursor.close();
		}
		return name;
	}
	/**
	 * 根据地址获取联系人的头像
	 * @param contentResolver
	 * @param address
	 * @return
	 */
	public static Bitmap getContactAvatarByAddress(ContentResolver contentResolver,String address){
		Bitmap avatar = null;
		//1.先获取联系人的id
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
		Cursor cursor = contentResolver.query(uri, new String[]{"_id"}, null, null, null);
		//2.根据联系人id获取头像。
		if(cursor!=null&&cursor.moveToNext()){
			int contactId = cursor.getInt(0);
			cursor.close();
			InputStream input = Contacts.openContactPhotoInputStream(contentResolver, ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId));
			avatar = BitmapFactory.decodeStream(input);
		}
		return avatar;
	}
	/**
	 *<strong>通过联系人的地址(电话)查询联系人的头像。</strong> 
	 *<p>这个方法想比较上一个方法来说，少一步查询数据库的操作，联系人id事先已经存放在map集合中</p>
	 * @param resolver
	 * @param address
	 * @return
	 */
	public static Bitmap getContactAvatarByCacheId(ContentResolver resolver,String address){
		Bitmap avatar = null;
		if(address!=null){
			//1.先获取联系人的id
			int contactId = ConversationFragment.numberIdMap.get(address.startsWith("+86")?address.substring(3):address);
			//2.根据联系人id获取相应头像。
			InputStream input = Contacts.openContactPhotoInputStream(resolver, ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId));
			avatar = BitmapFactory.decodeStream(input);
		}
		return avatar;
	}
	/**
	 * 通过联系人的id查询它的电话号码
	 * @param contentResolver
	 * @param contactId
	 * @return
	 */
	public static String getNumberById(ContentResolver contentResolver,int contactId) {
		String number = "";
		String[] projection = new String[]{"data1"};
		String selection = "contact_id="+contactId;
		Cursor cursor = contentResolver.query(Phone.CONTENT_URI, projection, selection, null, null);
		if(cursor!=null&&cursor.moveToNext()){
			number = cursor.getString(0);
			cursor.close();
		}
		return number;
	}
}
