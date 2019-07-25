package bd.football.coachbook.utils;

import android.util.Log;

public class BLog {

	private static final String TAG = "birdea";

	public static void d(String msg) {
		Log.d(TAG, msg);
	}

	public static void i(String msg) {
		Log.i(TAG, msg);
	}

	public static void w(String msg) {
		Log.w(TAG, msg);
	}

	public static void e(String msg) {
		Log.e(TAG, msg);
	}

	public static void w(String msg, Object message) {
		if (message == null) {
			Log.w(TAG, "null");
		} else if (message instanceof Throwable) {
			Throwable t = (Throwable) message;
			Log.w(TAG, t.getMessage(), t);
		} else {
			Log.w(TAG, message.toString());
		}
	}
	
	public static void e(Object message) {
		if (message == null) {
			Log.e(TAG, "null");
		} else if (message instanceof Throwable) {
			Throwable t = (Throwable) message;
			Log.e(TAG, t.getMessage(), t);
		} else {
			Log.e(TAG, message.toString());
		}
	}
}
