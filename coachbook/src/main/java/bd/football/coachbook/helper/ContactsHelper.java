	package bd.football.coachbook.helper;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import bd.football.coachbook.db.dao.MemberScheme.MemberParam;
import bd.football.coachbook.utils.BLog;

public class ContactsHelper {

	private static int lastReadCount = 0;
	private static long lastReadTime = 0;
	private static ArrayList<MemberParam> lastReadList = new ArrayList<MemberParam>();
	
	public static ArrayList<MemberParam> getContactList(Context context) {
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		ArrayList<MemberParam> contactlist = new ArrayList<MemberParam>();
		int count = cur.getCount();
		if (count > 0) {
			
			if(System.currentTimeMillis() - lastReadTime < 300 * 1000 && lastReadCount == count){
				contactlist.addAll(lastReadList);
				return contactlist;
			}
			
			while (cur.moveToNext()) {
				String cid = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photoId = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
				String photoUri = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
				String photoThumbUri = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
				String phoneNo = null;
				//
				MemberParam acontact = new MemberParam();
				acontact.cid = cid;
				acontact.name = name;
				acontact.photo_id = photoId;
				acontact.photo_uri = photoUri;
				acontact.photo_thumb_uri = photoThumbUri;
				//
				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
							new String[] { cid }, null);
					while (pCur.moveToNext()) {
						phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						acontact.phone_number = phoneNo;
						contactlist.add(acontact);
						break;
					}
					pCur.close();
				}
				BLog.d("getContactList - cid:" + cid + ", name:" + name + ", phone_number:" + phoneNo + ", photoUri:"+photoUri+", photoThumbUri:"+photoThumbUri);
			}
		}
		//
		lastReadTime = System.currentTimeMillis();
		lastReadCount = count;
		lastReadList.clear();
		lastReadList.addAll(contactlist);
		//
		return contactlist;
	}
}
