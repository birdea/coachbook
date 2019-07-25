package bd.football.coachbook.view;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import bd.football.coachbook.helper.TextPaintHelper;
import bd.football.coachbook.utils.BLog;

public class PlayTimerView extends RelativeLayout {

	public enum TimerMode {
		DefaultTimer("Def-Timer-ON"), CountDownTimerOFF("CD-Timer_OFF"), CountDownTimerON("CD-Timer-ON");

		private String modeName;

		TimerMode(String modeName) {
			this.modeName = modeName;
		}

		public String getTimerMode() {
			return modeName;
		}

		public boolean isCountDownTimer() {
			return (this.equals(CountDownTimerOFF) || this.equals(CountDownTimerON));
		}

	}

	OnCountTimerListener onCountTimerListener;

	public void setOnCountTimerListener(OnCountTimerListener onCountTimerListener) {
		this.onCountTimerListener = onCountTimerListener;
	}

	public interface OnCountTimerListener {
		public void onStart();

		public void onStop();
	}

	private TimerMode timerMode = TimerMode.DefaultTimer;

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

	private RectF rectfTimerArcHour = new RectF();
	private RectF rectfTimerArcMin = new RectF();
	private RectF rectfTimerArcSec = new RectF();
	private RectF rectfTimerArcSSec = new RectF();
	private RectF rectfTimerArcProgress = new RectF();

	private Paint paintTimerStartButton = new Paint();
	private Paint paintTimerStartButtonText = new Paint();
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
	//
	private Paint paintDelimeters = new Paint();
	private Paint paintProgressInner = new Paint();
	private Paint paintProgressOutter = new Paint();
	private float mScale = 0;
	//
	private int backGroundColor = Color.rgb(255, 255, 255);

	private void init(Context context) {
		//
		mScale = context.getResources().getDisplayMetrics().density;
		BLog.e("[PlayTimerView] scale:" + mScale);
		//
		timerMode = TimerMode.DefaultTimer;
		//
		setBackgroundColor(backGroundColor);
		//
		paintTimerText.setColor(Color.argb(177, 15, 15, 15));
		paintTimerText.setTypeface(Typeface.DEFAULT_BOLD);
		paintTimerText.setTextSize(100);
		paintTimerText.setAntiAlias(true);
		//
		paintTimerTextMilli.setColor(Color.argb(177, 65, 65, 65));
		paintTimerTextMilli.setTypeface(Typeface.DEFAULT_BOLD);
		paintTimerTextMilli.setTextSize(50);
		paintTimerTextMilli.setAntiAlias(true);
		//
		paintTimerModeText.setColor(Color.argb(177, 1, 1, 1));
		paintTimerModeText.setTypeface(Typeface.DEFAULT_BOLD);
		paintTimerModeText.setTextSize(15);
		paintTimerModeText.setAntiAlias(true);
		//
		paintTimerStartButton.setColor(Color.argb(177, 99, 11, 11));
		paintTimerStartButton.setAntiAlias(true);
		//
		paintTimerStartButtonText.setColor(Color.argb(177, 22, 33, 44));
		paintTimerStartButtonText.setAntiAlias(true);
		paintTimerStartButtonText.setTextSize(45);
		paintTimerStartButtonText.setTypeface(Typeface.DEFAULT_BOLD);
		//
		paintTimerCircle1.setColor(Color.argb(177, 111, 111, 111));
		paintTimerCircle1.setAntiAlias(true);
		//
		paintTimerCircle2.setColor(Color.argb(177, 62, 62, 62));
		paintTimerCircle2.setAntiAlias(true);
		//
		paintTimerArcHour.setColor(Color.argb(177, 0, 0, 0));
		paintTimerArcHour.setAntiAlias(true);
		paintTimerArcMin.setColor(Color.argb(177, 0, 0, 0));
		paintTimerArcMin.setAntiAlias(true);
		paintTimerArcSec.setColor(Color.argb(177, 255, 255, 255));
		paintTimerArcSec.setAntiAlias(true);
		paintTimerArcSSec.setColor(Color.argb(177, 22, 22, 22));
		paintTimerArcSSec.setAntiAlias(true);
		//
		paintTouchPoint.setColor(Color.argb(177, 211, 211, 211));
		paintTouchPoint.setAntiAlias(true);
		//
		paintDelimeters.setColor(Color.argb(177, 211, 155, 155));
		paintDelimeters.setAntiAlias(true);
		//
		paintProgressInner.setColor(Color.argb(177, 177, 122, 122));
		paintProgressInner.setStrokeWidth(2 * mScale);
		paintProgressInner.setAntiAlias(true);
		//
		paintProgressOutter.setColor(Color.argb(177, 199, 66, 66));
		paintProgressOutter.setStrokeWidth(2 * mScale);
		paintProgressOutter.setAntiAlias(true);
	}

	public void changeMode() {
		if (TimerMode.DefaultTimer.equals(timerMode)) {
			timerMode = TimerMode.CountDownTimerOFF;
		} else {
			timerMode = TimerMode.DefaultTimer;
		}
		invalidate();
	}

	public TimerMode getTimerMode() {
		return timerMode;
	}

	public void stopTimer() {
		stopTimerUpdate();
		if (false == timerMode.isCountDownTimer()) {
			startTimerUpdate();
		}
	}

	public void startTimer() {
		startTime = System.currentTimeMillis();
		reservedTime = startTime + countDownTime * 1000;
		startTimerUpdate();
	}

	public void setCountDownTime(long cdTimeSec) {
		countDownTime = cdTimeSec;
	}

	private static final int MSG_INTERVAL_NOTIFIER_UPDATE = 0x01;
	private int intervalTimerMs = 10;
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
			if (timerMode.isCountDownTimer()) {
				timerMode = TimerMode.CountDownTimerON;
			}
		}
	}

	private void stopTimerUpdate() {
		if (intervalNotifier == null) {
			return;
		}
		intervalNotifier.cancel();
		isNotified = false;
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
			renewClockTextSize();
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
		float rProgress = radius / 5 * 2 - 10;
		float rMSec = radius / 5 * 2 - 25;
		rectfTimerArcSSec.set(left + rMSec, top + rMSec, right - rMSec, bottom - rMSec);
		rectfTimerArcProgress.set(left + rProgress, top + rProgress, right - rProgress, bottom - rProgress);
	}

	private void renewClockTextSize() {
		String form = "00:00:00";
		float textSize = paintTimerText.getTextSize();
		float[] charWidths = new float[form.length()];
		float sumX = 0;
		float validLength = radius * 2;
		do {
			sumX = 0;
			paintTimerText.setTextSize(textSize--);
			paintTimerText.getTextWidths(form, charWidths);
			for (float widths : charWidths) {
				sumX += widths;
			}
		} while (sumX > validLength);
		paintTimerTextMilli.setTextSize(textSize / 2);
		BLog.e("renewClockTextSize :" + textSize + ", sumX:" + sumX + " , radius:" + radius + ", validLength:" + validLength);
	}

	private long getCurrentModeTimeValue() {
		if (TimerMode.DefaultTimer.equals(timerMode)) {
			return System.currentTimeMillis();
		} else {
			long time = reservedTime - System.currentTimeMillis();
			if (time <= 0) {
				if (onCountTimerListener != null && !isNotified) {
					if (TimerMode.CountDownTimerON.equals(timerMode)) {
						onCountTimerListener.onStop();
						timerMode = TimerMode.CountDownTimerOFF;
					}
					isNotified = true;
				}
				stopTimerUpdate();
				return 0;
			}
			return time;
		}
	}

	boolean isNotified = false;
	private long startTime;
	private long reservedTime;
	private long countDownTime;

	@Override
	protected void onDraw(Canvas canvas) {
		// set values;
		long timerTime = getCurrentModeTimeValue();
		// step.1 outter circle
		canvas.drawCircle(centerX, centerY, radius, paintTimerCircle1);
		drawBgDelimeters(canvas);
		// step.2 outter arc (HOUR : MIN : SEC : MilliSEC)
		drawTimerArcs(canvas, timerTime);
		// step.3 inner circle
		canvas.drawCircle(centerX, centerY, radius - 10, paintTimerCircle2);
		drawBgProgressBar(canvas);

			drawInnerStartButton(canvas);
		// step.4 text (HOUR : MIN : SEC : MilliSEC)
		drawTimerText(canvas, timerTime);
		drawTimerModeText(canvas);
		//
		drawTouchPoint(canvas);
		//
		super.onDraw(canvas);
	}

	private void drawBgProgressBar(Canvas canvas) {
		float progressRate = (float) ((double) (reservedTime - System.currentTimeMillis()) / (double) (reservedTime - startTime) * 360);
		// BLog.e("drawBgProgressBar progressRate:" + progressRate);
		if (progressRate < 0) {
			progressRate = 0;
		}
		if (progressRate > 360) {
			progressRate = 360;
		}
		canvas.drawCircle(centerX, centerY, radius / 5 * 3 + 10, paintProgressInner);
		canvas.drawArc(rectfTimerArcProgress, -90, progressRate, true, paintProgressOutter);
	}

	private void drawBgDelimeters(Canvas canvas) {
		// Common Set
		double x1 = 0f, y1 = 0f, x2 = 0f, y2 = 0f;
		double r1 = 0f, r2 = 0f;
		double r11 = radius + 2 * mScale;
		double r12 = radius - 15 * mScale;
		double r21 = radius + 2 * mScale;
		double r22 = radius - 12 * mScale;
		double r31 = radius + 2 * mScale;
		double r32 = radius - 10 * mScale;
		for (int degree = 0; degree <= 360; degree += 2) {
			if (degree % 30 == 0) {
				r1 = r11;
				r2 = r12;
				paintDelimeters.setColor(Color.argb(177, 122, 55, 55));
				paintDelimeters.setStrokeWidth(2 * mScale);
			} else if (degree % 6 == 0) {
				r1 = r21;
				r2 = r22;
				paintDelimeters.setColor(Color.argb(177, 122, 55, 55));
				paintDelimeters.setStrokeWidth(1 * mScale);
			} else {
				r1 = r31;
				r2 = r32;
				paintDelimeters.setColor(Color.argb(177, 155, 155, 155));
				paintDelimeters.setStrokeWidth(1 * mScale);
			}
			x1 = (centerX + r1 * Math.cos(Math.toRadians(degree)));
			y1 = (centerY + r1 * Math.sin(Math.toRadians(degree)));
			x2 = (centerX + r2 * Math.cos(Math.toRadians(degree)));
			y2 = (centerY + r2 * Math.sin(Math.toRadians(degree)));
			canvas.drawLine((float) x1, (float) y1, (float) x2, (float) y2, paintDelimeters);
		}
	}

	private void drawInnerStartButton(Canvas canvas) {
		//
		canvas.drawCircle(centerX, centerY, radius / 5 * 3, paintTimerStartButton);
		//
		if (timerMode.isCountDownTimer()) {
			String text = null;
			if (TimerMode.CountDownTimerOFF.equals(timerMode)) {
				text = "START";
			} else {
				text = "STOP";
			}
			float cordinateX = TextPaintHelper.getTextWidths(text, paintTimerStartButtonText);
			canvas.drawText(text, centerX - cordinateX / 2, centerY + radius / 3, paintTimerStartButtonText);
		}
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
		float vHour, vMin, vSec, vMSec;
		vMSec = cal.get(Calendar.MILLISECOND);
		vSec = cal.get(Calendar.SECOND);
		vMin = cal.get(Calendar.MINUTE);
		vHour = cal.get(Calendar.HOUR);
		float angleHour, angleMin, angleSec, angleMSec;
		angleMSec = getTimerArcAngle(999, vMSec);
		angleSec = getTimerArcAngle(60, vSec) + (6f * vMSec / 999f) - 0.5f;
		angleMin = getTimerArcAngle(60, vMin) + (0.1f * vSec) - 0.5f;
		angleHour = getTimerArcAngle(12, vHour) + (0.5f * vMin) - 0.5f;

		canvas.drawArc(rectfTimerArcMin, angleMin, 1, true, paintTimerArcMin);
		canvas.drawArc(rectfTimerArcSec, angleSec, 1, true, paintTimerArcSec);
		canvas.drawArc(rectfTimerArcSSec, angleMSec, 180, true, paintTimerArcSSec);
		
		if(!timerMode.isCountDownTimer()){
			canvas.drawArc(rectfTimerArcHour, angleHour, 1, true, paintTimerArcHour);
			canvas.save();
			Matrix matrix = new Matrix();
			matrix.preTranslate(centerX, centerY);
			matrix.preRotate(angleHour + 90 + 0.5f);
			canvas.concat(matrix);
			canvas.drawCircle(0, -radius+60, 15f, paintTimerArcHour);
			canvas.drawCircle(0, -radius+60, 25f, paintTimerArcHour);
			canvas.restore();
		}
		//
		canvas.save();
		Matrix matrix = new Matrix();
		matrix.preTranslate(centerX, centerY);
		matrix.preRotate(angleMin + 90 + 0.5f);
		canvas.concat(matrix);
		canvas.drawCircle(0, -radius+45, 15f, paintTimerArcMin);
		canvas.drawCircle(0, -radius+45, 25f, paintTimerArcMin);
		canvas.restore();
		//
		canvas.save();
		matrix = new Matrix();
		matrix.preTranslate(centerX, centerY);
		matrix.preRotate(angleSec + 90 + 0.5f);
		canvas.concat(matrix);
		canvas.drawCircle(0, -radius+30, 15f, paintTimerArcSec);
		canvas.drawCircle(0, -radius+30, 25f, paintTimerArcSec);
		canvas.restore();
		//BLog.e("vMin:" + vMin + ", angleMinC:" + angleMinC + ", vSec:" + vSec + ", angleSecC:" + angleSecC);
	}

	private float getTimerArcAngle(float max, float value) {
		return 360 * value / max - 90;
	}

	private String[] getCountDownText(long timerTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timerTime);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int milliSecond = cal.get(Calendar.MILLISECOND);
		String am_pm = "AM";
		if (cal.get(Calendar.AM_PM) == Calendar.PM) {
			am_pm = "PM";
		}
		if (timerMode.isCountDownTimer()) {
			hour = 0;
		}
		String[] times = { String.format("%02d", hour), String.format("%02d", minute), String.format("%02d", second), String.format("%03d", milliSecond), am_pm };
		return times;
	}

	private void drawTimerText(Canvas canvas, long timerTime) {
		String[] texts = getCountDownText(timerTime);
		String fullText;
		if (timerMode.isCountDownTimer()) {
			fullText = texts[1] + ":" + texts[2];
		}else{
			fullText = texts[0] + ":" + texts[1] + ":" + texts[2];
		}
		// fullText = fullText.substring(0, fullText.length() - 2);
		float[] charWidths = new float[fullText.length()];
		paintTimerText.getTextWidths(fullText, charWidths);
		float sumX = 0;
		for (float widths : charWidths) {
			sumX += widths;
		}
		sumX = sumX / 2;
		if (!timerMode.isCountDownTimer()) {
			float cordinateX = TextPaintHelper.getTextWidths(texts[4], paintTimerTextMilli);
			// AM or PM
			canvas.drawText(texts[4], centerX - cordinateX / 2, centerY - 50, paintTimerTextMilli);
		}
		canvas.drawText(texts[3], centerX + sumX, centerY + 30, paintTimerTextMilli);
		canvas.drawText(fullText, centerX - sumX, centerY + 30, paintTimerText);
		lastMinSec = texts[1] + ":" + texts[2];
	}
	
	private String lastMinSec = "";
	
	public String getLastMinSecValue() {
		return lastMinSec;
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
		return true;// super.onTouchEvent(event);
	}

	private boolean isValidClickStartButton = false;

	private void handleTouchActionUp(MotionEvent event) {
		touchX = getTouchX(event);
		touchY = getTouchY(event);
		//
		paintTimerStartButton.setColor(Color.argb(155, 99, 11, 11));
		//
		if (isValidClickStartButton && timerMode.isCountDownTimer()) {
			if (TimerMode.CountDownTimerON.equals(timerMode)) {
				timerMode = TimerMode.CountDownTimerOFF;
				stopTimer();
			} else {
				startTimer();
			}
		}
		invalidate();
	}

	private void handleTouchActionMove(MotionEvent event) {
		touchX = getTouchX(event);
		touchY = getTouchY(event);
		if (isGrabPointOnChip(touchX, touchY)) {
			isValidClickStartButton = true;
			paintTimerStartButton.setColor(Color.argb(155, 199, 111, 111));
		} else {
			isValidClickStartButton = false;
			paintTimerStartButton.setColor(Color.argb(155, 99, 11, 11));
		}
		invalidate();
	}

	private void handleTouchActionDown(MotionEvent event) {
		touchX = getTouchX(event);
		touchY = getTouchY(event);
		if (isGrabPointOnChip(touchX, touchY)) {
			isValidClickStartButton = true;
			paintTimerStartButton.setColor(Color.argb(155, 199, 111, 111));
		} else {
			isValidClickStartButton = false;
			paintTimerStartButton.setColor(Color.argb(155, 99, 11, 11));
		}
		invalidate();
	}

	private float getTouchX(MotionEvent event) {
		return event.getX() - left;
	}

	private float getTouchY(MotionEvent event) {
		return event.getY() - top;
	}

	private boolean isGrabPointOnChip(float touchX, float touchY) {
		Rect rect = new Rect();
		float r = radius / 5 * 3;
		rect.set((int) (centerX - r), (int) (centerY - r), (int) (centerX + r), (int) (centerY + r));
		return rect.contains((int) touchX, (int) touchY);
	}

}
