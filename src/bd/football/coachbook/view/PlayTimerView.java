package bd.football.coachbook.view;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import bd.football.coachbook.utils.BLog;

public class PlayTimerView extends RelativeLayout {

	public enum TimerMode {
		OFF("Timer-OFF"), DefaultTime("Default-Timer"), CountDownTime("CountDown-Timer");
		private String modeName;

		TimerMode(String modeName) {
			this.modeName = modeName;
		}

		public String getTimerMode() {
			return modeName;
		}

	}

	private TimerMode timerMode = TimerMode.DefaultTime;

	public PlayTimerView(Context context) {
		super(context);
		init(context);
	}

	public PlayTimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PlayTimerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private int backGroundColor = Color.rgb(0, 0, 0);

	private RectF rectfTimerArcHour = new RectF();
	private RectF rectfTimerArcMin = new RectF();
	private RectF rectfTimerArcSec = new RectF();
	private RectF rectfTimerArcSSec = new RectF();

	private Paint paintTimerCircle1 = new Paint();
	private Paint paintTimerCircle2 = new Paint();
	//
	private Paint paintTimerArcHour = new Paint();
	private Paint paintTimerArcMin = new Paint();
	private Paint paintTimerArcSec = new Paint();
	private Paint paintTimerArcSSec = new Paint();
	//
	private Paint paintTimerText = new Paint();
	private Paint paintTimerTextMilli = new Paint();
	private Paint paintTimerModeText = new Paint();
	private Paint paintTouchPoint = new Paint();

	private void init(Context context) {
		timerMode = TimerMode.DefaultTime;
		//
		setBackgroundColor(backGroundColor);
		//
		paintTimerText.setColor(Color.rgb(255, 255, 255));
		paintTimerText.setTypeface(Typeface.DEFAULT_BOLD);
		paintTimerText.setTextSize(100);
		paintTimerText.setAntiAlias(true);
		//
		paintTimerTextMilli.setColor(Color.rgb(155, 155, 155));
		paintTimerTextMilli.setTypeface(Typeface.DEFAULT_BOLD);
		paintTimerTextMilli.setTextSize(50);
		paintTimerTextMilli.setAntiAlias(true);
		//
		paintTimerModeText.setColor(Color.rgb(255, 255, 255));
		paintTimerModeText.setTypeface(Typeface.DEFAULT_BOLD);
		paintTimerModeText.setTextSize(15);
		paintTimerModeText.setAntiAlias(true);
		//
		paintTimerCircle1.setColor(Color.rgb(111, 111, 111));
		paintTimerCircle1.setAntiAlias(true);
		//
		paintTimerCircle2.setColor(Color.rgb(62, 62, 62));
		paintTimerCircle2.setAntiAlias(true);
		//
		paintTimerArcHour.setColor(Color.rgb(0, 0, 0));
		paintTimerArcHour.setAntiAlias(true);
		paintTimerArcMin.setColor(Color.rgb(0, 0, 0));
		paintTimerArcMin.setAntiAlias(true);
		paintTimerArcSec.setColor(Color.rgb(255, 255, 255));
		paintTimerArcSec.setAntiAlias(true);
		paintTimerArcSSec.setColor(Color.rgb(22, 22, 22));
		paintTimerArcSSec.setAntiAlias(true);
		//
		paintTouchPoint.setColor(Color.rgb(211, 211, 211));
		paintTouchPoint.setAntiAlias(true);
	}

	public void changeMode() {
		if (TimerMode.DefaultTime.equals(timerMode)) {
			timerMode = TimerMode.CountDownTime;
		} else {
			timerMode = TimerMode.DefaultTime;
		}
	}

	public void stopTimer() {
		stopTimerUpdate();
	}

	public void startTimer() {
		countDownTime = System.currentTimeMillis() + 5 * 1000;
		startTimerUpdate();
	}

	private static final int MSG_INTERVAL_NOTIFIER_UPDATE = 0x01;
	private int intervalTimerMs = 1;
	private Timer intervalNotifier;

	private class IntervalNotifier extends TimerTask {
		@Override
		public void run() {
			uiHandler.sendEmptyMessage(MSG_INTERVAL_NOTIFIER_UPDATE);
		}
	}

	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_INTERVAL_NOTIFIER_UPDATE) {
				invalidate();
			}
			super.handleMessage(msg);
		}
	};

	public void setTimerUpdateInterval(int interval) {
		if (intervalTimerMs != interval) {
			intervalTimerMs = interval;
		}
	}

	private void startTimerUpdate() {
		stopTimerUpdate();
		if (intervalTimerMs > 0) {
			intervalNotifier = new Timer();
			intervalNotifier.schedule(new IntervalNotifier(), 0, intervalTimerMs);
		}
	}

	private void stopTimerUpdate() {
		if (intervalNotifier == null) {
			return;
		}
		intervalNotifier.cancel();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		startTimerUpdate();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stopTimerUpdate();
	}

	private int left, top, right, bottom;
	private int centerX, centerY;
	private int viewWidth, viewHeight;
	private float radius;

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		boolean isFirst = (0 == left && 0 == top && 0 == right && 0 == bottom);
		left = l;
		top = t;
		right = r;
		bottom = b;
		if (changed || isFirst) {
			viewWidth = right - left;
			viewHeight = bottom - top;
			centerX = (right + left) / 2;
			centerY = (top + bottom) / 2;
			//
			radius = Math.min(viewWidth, viewHeight) / 2f - 5;
			initRectTimerAcrs();
			// rectTimerArc1.set((int)(centerX-radius), (int)(centerY-radius),
			// (int)(centerX+radius), (int)(centerY+radius));
		}
		BLog.e("onLayout left:" + left + ", top:" + top + ", right:" + right + ", bottom:" + bottom);
		super.onLayout(changed, l, t, r, b);
	}

	private void initRectTimerAcrs() {
		float left = centerX - radius - 2;
		float right = centerX + radius + 2;
		float top = centerY - radius - 2;
		float bottom = centerY + radius + 2;
		rectfTimerArcHour.set(left, top, right, bottom);
		rectfTimerArcMin.set(left, top, right, bottom);
		rectfTimerArcSec.set(left, top, right, bottom);
		rectfTimerArcSSec.set(left + 4, top + 4, right - 4, bottom - 4);
	}

	private long getCurrentModeTimeValue() {
		if (TimerMode.DefaultTime.equals(timerMode)) {
			return System.currentTimeMillis();
		} else {
			long time = countDownTime - System.currentTimeMillis();
			if (time < 0) {
				return 0;
			}
			return time;
		}
	}

	private long countDownTime;

	@Override
	protected void onDraw(Canvas canvas) {
		// set values;
		long timerTime = getCurrentModeTimeValue();
		// step.1 outter circle
		canvas.drawCircle(centerX, centerY, radius, paintTimerCircle1);
		// step.2 outter arc (HOUR : MIN : SEC : MilliSEC)
		drawTimerArcs(canvas, timerTime);
		// step.3 inner circle
		canvas.drawCircle(centerX, centerY, radius - 10, paintTimerCircle2);
		// step.4 text (HOUR : MIN : SEC : MilliSEC)
		drawTimerText(canvas, timerTime);
		drawTimerModeText(canvas);
		//
		drawTouchPoint(canvas);
		super.onDraw(canvas);
	}

	private void drawTouchPoint(Canvas canvas) {
		if (TouchEvent.DOWN.equals(touchMode) || TouchEvent.MOVE.equals(touchMode)) {
			canvas.drawCircle(touchX, touchY, 25, paintTouchPoint);
		}
	}

	private void drawTimerModeText(Canvas canvas) {
		canvas.drawText("Mode:" + timerMode.getTimerMode() + "/Interval:" + intervalTimerMs, 10, 15, paintTimerModeText);
	}

	private void drawTimerArcs(Canvas canvas, long currentTime) {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(currentTime);
		float angleHour, angleMin, angleSec, angleMSec;
		angleMSec = getTimerArcAngle(999, cal.get(Calendar.MILLISECOND));
		angleSec = getTimerArcAngle(60, cal.get(Calendar.SECOND));
		angleMin = getTimerArcAngle(60, cal.get(Calendar.MINUTE));
		angleHour = getTimerArcAngle(12, cal.get(Calendar.HOUR));
		canvas.drawArc(rectfTimerArcHour, angleHour, 1, true, paintTimerArcHour);
		canvas.drawArc(rectfTimerArcMin, angleMin, 1, true, paintTimerArcMin);
		canvas.drawArc(rectfTimerArcSec, angleSec, 1, true, paintTimerArcSec);
		canvas.drawArc(rectfTimerArcSSec, angleMSec, 180, true, paintTimerArcSSec);
	}

	private float getTimerArcAngle(float max, float value) {
		return 360 * value / max - 90;
	}

	// private SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm:ss.SSS",
	// Locale.getDefault());

	private String[] getCountDownText(long timerTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timerTime);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int milliSecond = cal.get(Calendar.MILLISECOND);
		String am_pm = "AM";
		if(cal.get(Calendar.AM_PM) == Calendar.PM){
			am_pm = "PM";
		}
		String[] times = { String.format("%02d", hour), String.format("%02d",minute), String.format("%02d", second), String.format("%03d",milliSecond), am_pm };
		return times;
	}

	private void drawTimerText(Canvas canvas, long timerTime) {
		String[] texts = getCountDownText(timerTime);
		String fullText = texts[0]+":"+texts[1]+":"+texts[2];//+":"+texts[3];
		//fullText = fullText.substring(0, fullText.length() - 2);
		float[] charWidths = new float[fullText.length()];
		paintTimerText.getTextWidths(fullText, charWidths);
		float sumX = 0;
		for (float widths : charWidths) {
			sumX += widths;
		}
		sumX = sumX / 2;
		// AM or PM
		canvas.drawText(texts[4], centerX - sumX - 80, centerY, paintTimerTextMilli);
		// milli sec
		canvas.drawText(texts[3], centerX + sumX, centerY + 30, paintTimerTextMilli);
		// hour
		canvas.drawText(fullText, centerX - sumX, centerY + 30, paintTimerText);
		// min

		// sec
	}

	private enum TouchEvent {
		NONE, DOWN, MOVE, UP;
	}

	private TouchEvent touchMode = TouchEvent.NONE;
	private float touchX, touchY;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		// BDLog.i("onTouchEvent on view:" + event);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			touchMode = TouchEvent.DOWN;
			handleTouchActionDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			touchMode = TouchEvent.MOVE;
			handleTouchActionMove(event);
			break;
		case MotionEvent.ACTION_UP:
			touchMode = TouchEvent.UP;
			handleTouchActionUp(event);
			break;
		}
		return super.onTouchEvent(event);
	}

	private void handleTouchActionUp(MotionEvent event) {
		touchX = getTouchX(event);
		touchY = getTouchY(event);
	}

	private void handleTouchActionMove(MotionEvent event) {
		touchX = getTouchX(event);
		touchY = getTouchY(event);
	}

	private void handleTouchActionDown(MotionEvent event) {
		touchX = getTouchX(event);
		touchY = getTouchY(event);
	}

	private float getTouchX(MotionEvent event) {
		return event.getX() - left;
	}

	private float getTouchY(MotionEvent event) {
		return event.getY() - top;
	}
}
