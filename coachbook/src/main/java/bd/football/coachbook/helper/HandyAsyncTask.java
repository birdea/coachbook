package bd.football.coachbook.helper;

import bd.football.coachbook.AppConfig;
import bd.football.coachbook.except.AssertException;
import bd.football.coachbook.except.CancelledException;
import bd.football.coachbook.helper.HandyAsyncTaskHelper.Cancelable;
import bd.football.coachbook.helper.HandyAsyncTaskHelper.ExceptionAware;
import bd.football.coachbook.utils.BLog;

public class HandyAsyncTask extends CustomAsyncTask<Void, Void, Boolean> implements Cancelable, ExceptionAware {
	private Exception exception = null;
	private HandyAsyncCommand asyncCmd;

	public HandyAsyncTask(HandyAsyncCommand asyncCmd) {
		AssertException.assertNotNull(asyncCmd);
		this.asyncCmd = asyncCmd;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (AppConfig.isDebug()) {
			BLog.d(String.format("BEGIN doInBackground (thread : %s, cmd : %s)", Thread.currentThread().getName(), this.asyncCmd));
		}
		try {
			return HandyAsyncTaskHelper.runCommand(asyncCmd, this);
		} finally {
			if (AppConfig.isDebug()) {
				BLog.d(String.format("END doInBackground (thread : %s, cmd : %s)", Thread.currentThread().getName(), this.asyncCmd));
			}
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result == null) {
			result = false;
		}
		asyncCmd.onResult(result, exception);
	}

	public void execute() {
		try {
			executeMutiThreaded();
		} catch (Exception e) {
			BLog.w("Exception", e);
			exception = e;
			onPostExecute(false);
		}
	}

	@Override
	public void cancel() {
		if (AppConfig.isDebug()) {
			BLog.w(String.format("cancel (cmd : %s)", this.asyncCmd));
		}
		cancel(true);
	}

	protected void onCancelled() {
		asyncCmd.onResult(false, new CancelledException());
	}

	@Override
	public void setException(Exception e) {
		this.exception = e;
	}
}
