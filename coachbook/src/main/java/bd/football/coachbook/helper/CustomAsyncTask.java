package bd.football.coachbook.helper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.Message;
import bd.football.coachbook.AppConfig;
import bd.football.coachbook.helper.HandyAsyncTaskHelper.RejectionAware;

public abstract class CustomAsyncTask<Params, Progress, Result> implements RejectionAware {
	//
	private static final BlockingQueue<Runnable> multiThreadWorkQueue = new LinkedBlockingQueue<Runnable>(10);
	//
	private static final int MESSAGE_POST_RESULT = 0x1;
	private static final int MESSAGE_POST_PROGRESS = 0x2;
	private static final InternalHandler HANDLER = new InternalHandler();
	private final WorkerRunnable<Params, Result> mWorker;
	private final FutureTask<Result> mFuture;
	private volatile Status mStatus = Status.PENDING;
	//
	private final AtomicBoolean mTaskInvoked = new AtomicBoolean();
	private static final int KEEP_ALIVE = 1;

	private static final ThreadFactory singleThreadFactoy = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "SingleAsyncTask #" + mCount.getAndIncrement());
		}
	};

	public static Executor getNewSingleThreadExecutor() {
		return getNewSingleThreadExecutor(10);
	}

	public static Executor getNewSingleThreadExecutor(int queueSize) {
		return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(queueSize), singleThreadFactoy, new DiscardOldestPolicyWithNotification());
	}

	private static final ThreadFactory multiThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
		}
	};

	public static Executor getNewMultiThreadExecutor() {
		return new ThreadPoolExecutor(5, 128, KEEP_ALIVE, TimeUnit.SECONDS, multiThreadWorkQueue, multiThreadFactory);
	}

	public static final Executor SINGLE_THREAD_EXECUTOR = getNewSingleThreadExecutor();
	public static final Executor MULTI_THREADED_EXECUTOR = getNewMultiThreadExecutor();

	static class DiscardOldestPolicyWithNotification implements RejectedExecutionHandler {
		public DiscardOldestPolicyWithNotification() {
		}

		public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
			if (!e.isShutdown()) {
				Runnable oldest = e.getQueue().poll();
				e.execute(r);
				if (AppConfig.isDebug()) {
				}
				if (oldest instanceof RejectionAware) {
					RejectionAware rejectionAware = (RejectionAware) oldest;
					rejectionAware.onRejected();
				}
			}
		}
	}

	@Override
	public void onRejected() {
	}

	static class FutureTaskRejectionAware<V> extends FutureTask<V> implements RejectionAware {
		public RejectionAware rejectionAware;

		public FutureTaskRejectionAware(Callable<V> callable, RejectionAware rejectionAware) {
			super(callable);
			this.rejectionAware = rejectionAware;
		}

		@Override
		public void onRejected() {
			rejectionAware.onRejected();
		}
	}

	/** @hide Used to force static handler to be created. */
	public static void init() {
		HANDLER.getLooper();
	}

	public CustomAsyncTask() {
		mWorker = new WorkerRunnable<Params, Result>() {
			public Result call() throws Exception {
				mTaskInvoked.set(true);
				return postResult(doInBackground(mParams));
			}
		};

		mFuture = new FutureTaskRejectionAware<Result>(mWorker, this) {
			@Override
			protected void done() {
				try {
					final Result result = get();

					postResultIfNotInvoked(result);
				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
					throw new RuntimeException("An error occured while executing doInBackground()", e.getCause());
				} catch (CancellationException e) {
					postResultIfNotInvoked(null);
				} catch (Throwable t) {
					throw new RuntimeException("An error occured while executing " + "doInBackground()", t);
				}
			}
		};
	}

	private void postResultIfNotInvoked(Result result) {
		final boolean wasTaskInvoked = mTaskInvoked.get();
		if (!wasTaskInvoked) {
			postResult(result);
		}
	}

	@SuppressWarnings("unchecked")
	private Result postResult(Result result) {
		Message message = HANDLER.obtainMessage(MESSAGE_POST_RESULT, new AsyncTaskResult<Result>(this, result));
		message.sendToTarget();
		return result;
	}

	public final Status getStatus() {
		return mStatus;
	}

	protected abstract Result doInBackground(Params... params);
	
	protected void onPreExecute() {
	}

	protected void onPostExecute(Result result) {
	}

	protected void onProgressUpdate(Progress... values) {
	}

	protected void onCancelled(Result result) {
		onCancelled();
	}

	protected void onCancelled() {
	}

	public final boolean isCancelled() {
		return mFuture.isCancelled();
	}

	public final boolean cancel(boolean mayInterruptIfRunning) {
		return mFuture.cancel(mayInterruptIfRunning);
	}

	public final Result get() throws InterruptedException, ExecutionException {
		return mFuture.get();
	}

	public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return mFuture.get(timeout, unit);
	}

	public final CustomAsyncTask<Params, Progress, Result> executeSingleThreaded(Params... params) {
		return executeOnExecutor(SINGLE_THREAD_EXECUTOR, params);
	}

	public final CustomAsyncTask<Params, Progress, Result> executeMutiThreaded(Params... params) {
		return executeOnExecutor(MULTI_THREADED_EXECUTOR, params);
	}

	public final CustomAsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
		if (mStatus != Status.PENDING) {
			switch (mStatus) {
			case RUNNING:
				throw new IllegalStateException("Cannot execute task:" + " the task is already running.");
			case FINISHED:
				throw new IllegalStateException("Cannot execute task:" + " the task has already been executed " + "(a task can be executed only once)");
			default:
				break;
			}
		}
		mStatus = Status.RUNNING;
		onPreExecute();
		mWorker.mParams = params;
		exec.execute(mFuture);
		return this;
	}

	public static void execute(Runnable runnable) {
		SINGLE_THREAD_EXECUTOR.execute(runnable);
	}
	
	protected final void publishProgress(Progress... values) {
		if (!isCancelled()) {
			HANDLER.obtainMessage(MESSAGE_POST_PROGRESS, new AsyncTaskResult<Progress>(this, values)).sendToTarget();
		}
	}

	private void finish(Result result) {
		if (isCancelled()) {
			onCancelled(result);
		} else {
			onPostExecute(result);
		}
		mStatus = Status.FINISHED;
	}

	private static class InternalHandler extends Handler {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			@SuppressWarnings("rawtypes")
			AsyncTaskResult result = (AsyncTaskResult) msg.obj;
			switch (msg.what) {
			case MESSAGE_POST_RESULT:
				result.mTask.finish(result.mData[0]);
				break;
			case MESSAGE_POST_PROGRESS:
				result.mTask.onProgressUpdate(result.mData);
				break;
			}
		}
	}

	private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
		Params[] mParams;
	}

	private static class AsyncTaskResult<Data> {
		@SuppressWarnings("rawtypes")
		final CustomAsyncTask mTask;
		final Data[] mData;

		@SuppressWarnings("rawtypes")
		AsyncTaskResult(CustomAsyncTask task, Data... data) {
			mTask = task;
			mData = data;
		}
	}

}
