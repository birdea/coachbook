package bd.football.coachbook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.SystemClock;
import bd.football.coachbook.db.dao.MemberScheme;
import bd.football.coachbook.db.dao.DBLoader;
import bd.football.coachbook.except.AssertException;
import bd.football.coachbook.utils.BLog;

public class DBContainer implements DBLoader {

	DBOpenHelper dbHelper;
	SQLiteDatabase db;

	public MemberScheme mMemberDao;

	public DBContainer(Context context) {
		dbHelper = new DBOpenHelper(context);
		mMemberDao = new MemberScheme(this);
		//
		doLazyLoad();
	}

	public void close() {
		if (db == null || !db.isOpen()) {
			return;
		}
		BLog.d("DBContainer.close");
		try {
			db.close();
			if (dbHelper != null) {
				dbHelper.close();
			}
		} catch (Exception e) {
			BLog.w("Exception", e);
		}
		db = null;
	}

	private static final int MAX_RETRY = 5;
	private static final int SLEEP = 300;

	@Override
	public synchronized void doLazyLoad() {
		open();
	}

	private void open() {
		if (isOpened()) {
			return;
		}
		BLog.d("DBContainer.open");
		getWritableDatabase(0);
	}

	boolean isOpened() {
		return (db != null && db.isOpen());
	}

	private void getWritableDatabase(int retryCnt) {
		if (dbHelper == null) {
			return;
		}

		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			if (retryCnt > MAX_RETRY) {
				throw e;
			}
			SystemClock.sleep(SLEEP);
			getWritableDatabase(++retryCnt);
		}
	}

	@Override
	public SQLiteDatabase getDB() {
		AssertException.assertNotNull(db);
		return db;
	}
}
