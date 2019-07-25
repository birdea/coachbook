package bd.football.coachbook.db.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import bd.football.coachbook.db.dao.MemberScheme.MemberTable.COLUMNS;
import bd.football.coachbook.utils.BLog;

public class MemberScheme extends BaseScheme {

	public MemberScheme(DBLoader sqldb) {
		super(sqldb);
	}

	public static class MemberTable implements PopulatableTable {
		public static final String TABLE_NAME = "member_table";

		public static class COLUMNS implements BaseColumns {
			public static final String NAME = "name";
			public static final String TYPE = "type";
			public static final String AGE = "age";
			public static final String PLAYNUMBER = "play_number";
			public static final String PHONENUMBER = "phone_number";
			public static final String EMAIL = "email";
			public static final String POSITION = "position";
			public static final String PHOTO_PATH = "photo_path";
		}

		@Override
		public void populate(SQLiteDatabase db) {
			BLog.e("== MemberTable populate");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			String sql = "CREATE TABLE " + TABLE_NAME + " (" + COLUMNS._ID + " INTEGER PRIMARY KEY, " + COLUMNS.NAME + " TEXT, " + COLUMNS.TYPE + " TEXT, " + COLUMNS.AGE
					+ " INTEGER, " + COLUMNS.PLAYNUMBER + " INTEGER, " + COLUMNS.PHONENUMBER + " TEXT, " + COLUMNS.EMAIL + " TEXT, " + COLUMNS.PHOTO_PATH + " TEXT, "
					+ COLUMNS.POSITION + " INTEGER);";
			db.execSQL(sql);
		}
	}

	public static class MemberParam {
		public int id;
		public String cid; // contacts id
		public String name;
		public int type;
		public int age;
		public int play_number;
		public String phone_number;
		public String email;
		public int position;
		public String photo_id;
		public String photo_uri;
		public String photo_thumb_uri;
	}

	public ArrayList<MemberParam> getPlayMembers() {
		ArrayList<MemberParam> members = new ArrayList<MemberParam>();
		Cursor cursor = getDB().query(MemberTable.TABLE_NAME, null, null, null, null, null, "_id desc");

		try {
			int count = cursor.getCount();
			if (count == 0) {
				return members;
			}

			final int idColumnIdx = cursor.getColumnIndex(COLUMNS._ID);
			final int nameColumnIdx = cursor.getColumnIndex(COLUMNS.NAME);
			final int typeColumnIdx = cursor.getColumnIndex(COLUMNS.TYPE);
			final int ageColumnIdx = cursor.getColumnIndex(COLUMNS.AGE);
			final int playnumberColumnIdx = cursor.getColumnIndex(COLUMNS.PLAYNUMBER);
			final int phonenumberColumnIdx = cursor.getColumnIndex(COLUMNS.PHONENUMBER);
			final int emailColumnIdx = cursor.getColumnIndex(COLUMNS.EMAIL);
			final int photoColumnIdx = cursor.getColumnIndex(COLUMNS.PHOTO_PATH);

			BLog.i("getPlayMembers result - " + count);
			while (cursor.moveToNext()) {
				MemberParam item = new MemberParam();
				item.id = cursor.getInt(idColumnIdx);
				item.name = cursor.getString(nameColumnIdx);
				item.type = cursor.getInt(typeColumnIdx);
				item.age = cursor.getInt(ageColumnIdx);
				item.play_number = cursor.getInt(playnumberColumnIdx);
				item.phone_number = cursor.getString(phonenumberColumnIdx);
				item.email = cursor.getString(emailColumnIdx);
				item.photo_uri = cursor.getString(photoColumnIdx);
				members.add(item);
			}
			return members;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return members;
	}

	public void addMember(MemberParam param) {
		ContentValues values = new ContentValues();
		values.put(COLUMNS.NAME, param.name);
		values.put(COLUMNS.TYPE, param.type);
		values.put(COLUMNS.AGE, param.age);
		values.put(COLUMNS.PLAYNUMBER, param.play_number);
		values.put(COLUMNS.PHONENUMBER, param.phone_number);
		values.put(COLUMNS.EMAIL, param.email);
		values.put(COLUMNS.POSITION, param.position);
		values.put(COLUMNS.PHOTO_PATH, param.photo_uri);
		getDB().insert(MemberTable.TABLE_NAME, null, values);
	}

	public boolean removeMember(MemberParam param) {
		if (param == null) {
			return false;
		}
		String delWhere = COLUMNS.TYPE + "=? AND " + COLUMNS._ID + "=? AND" + COLUMNS.NAME + "=?";
		String[] delArgs = { Integer.toString(param.type), Integer.toString(param.id), param.name };
		int resultRow = getDB().delete(MemberTable.TABLE_NAME, delWhere, delArgs);
		return (resultRow > 0);
	}

	public void modifyMember() {

	}
}
