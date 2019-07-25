package bd.football.coachbook.db.dao;

import android.database.sqlite.SQLiteDatabase;

public interface PopulatableTable {
	void populate(SQLiteDatabase db);
}
