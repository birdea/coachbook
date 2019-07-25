package bd.football.coachbook.db.dao;

import android.database.sqlite.SQLiteDatabase;

public class BaseScheme {

	protected DBLoader loader;
	
	BaseScheme(DBLoader sqldb){
		this.loader = sqldb;
	}
	
	SQLiteDatabase getDB() {
		return loader.getDB();
	}
	
	void doLazyLoad() {
		loader.doLazyLoad();
	}
	
}
