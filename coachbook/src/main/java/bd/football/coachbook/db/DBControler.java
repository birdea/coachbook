package bd.football.coachbook.db;

import bd.football.coachbook.utils.BLog;
import android.content.Context;

public class DBControler {

	private static DBContainer mDBContainer;
	
	private DBControler() {
	}
	
	public static void open(Context context){
		BLog.e("== DBControler open");
		mDBContainer = new DBContainer(context);
	}
	
	public static DBContainer getDBContainer(){
		return mDBContainer;
	}
	
	public static void close() {
		mDBContainer.close();
	}
}
