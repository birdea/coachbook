package bd.football.coachbook.db.dao;

import android.database.sqlite.SQLiteDatabase;

public interface DBLoader {
	void doLazyLoad();

	SQLiteDatabase getDB();
}
