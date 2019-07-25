package bd.football.coachbook;

import bd.football.coachbook.db.DBControler;
import bd.football.coachbook.utils.BLog;
import android.app.Application;

public class MainApplication extends Application{

	@Override
	public void onCreate() {
		BLog.e("== MainApplication onCreate");
		DBControler.open(getApplicationContext());
	}
}
