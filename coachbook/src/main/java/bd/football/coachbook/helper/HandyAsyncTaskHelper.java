package bd.football.coachbook.helper;

import bd.football.coachbook.except.CancelledException;
import bd.football.coachbook.except.OutOfMemoryException;
import bd.football.coachbook.except.ThrowableException;
import bd.football.coachbook.utils.BLog;

public class HandyAsyncTaskHelper {

	public static interface ExceptionAware {
		void setException(Exception e);
	}

	public static interface Cancelable {
		void cancel();
	}
	
	public static interface RejectionAware {
		void onRejected();
	}

	public static boolean runCommand(HandyAsyncCommand asyncCmd, ExceptionAware ea) {
		try {
			boolean result = asyncCmd.executeExceptionSafely();
			return result;
		} catch (CancelledException e) {
			ea.setException(e);
			BLog.w("task is cancelled");
			return false;
		} catch (OutOfMemoryError e) {
			ea.setException(new OutOfMemoryException(e));
			BLog.w("OutOfMemoryError", e);
			return false;
		} catch (Exception e) {
			ea.setException(e);
			BLog.w("exception", e);
			return false;
		} catch (Throwable t) {
			ea.setException(new ThrowableException(t));
			BLog.w("throwable", t);
			return false;
		}
	}
}
