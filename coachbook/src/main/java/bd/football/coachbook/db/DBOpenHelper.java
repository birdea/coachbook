package bd.football.coachbook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import bd.football.coachbook.db.dao.MemberScheme.MemberTable;
import bd.football.coachbook.db.dao.PopulatableTable;
import bd.football.coachbook.utils.BLog;

public class DBOpenHelper extends SQLiteOpenHelper {

	static final int DB_VERSION = 1;
	static final String DB_NAME = "coachbook.db";
	static final String DEBUG_PATH = "/mnt/sdcard/";//Environment.getExternalStorageDirectory().getPath();

	public DBOpenHelper(Context context) {
		super(context, /*DEBUG_PATH + */DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		BLog.e("== DBOpenHelper onCreate");
		createOrUpgradeTables(db, -1);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		BLog.e("== DBOpenHelper onUpgrade // oldVersion:"+oldVersion+"/newVersion:"+newVersion);
		if (oldVersion >= newVersion) {
			return;
		}
		BLog.i("DBOpenHelper.onUpgrade oldVersion = " + oldVersion
				+ ", newVersion = " + newVersion);
		createOrUpgradeTables(db, oldVersion);
	}
	
	private void createOrUpgradeTables(SQLiteDatabase db, int oldVersion){
		if (oldVersion < 1) {
			PopulatableTable populatableTables[] = {new MemberTable()};
			populateTables(db, populatableTables);
		}
	}

	private void populateTables(SQLiteDatabase db, PopulatableTable[] populatableTables) {
		for (PopulatableTable populatableTable : populatableTables) {
			BLog.d("populateTable " + populatableTable);
			populatableTable.populate(db);
		}
	}
}
