package bd.football.coachbook.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import bd.football.coachbook.R;
import bd.football.coachbook.helper.PlayerChipHelper;
import bd.football.coachbook.model.PlayerChip;
import bd.football.coachbook.model.TeamFormation;
import bd.football.coachbook.rules.PlayerChipInterface;
import bd.football.coachbook.utils.BDLog;

public class PlayGroundView extends RelativeLayout implements PlayerChipInterface {

	public PlayGroundView(Context context) {
		super(context);
		init(context);
	}

	public PlayGroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PlayGroundView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private int backGroundColor = Color.rgb(33, 155, 33);
	private Paint paintPlayerChipCircle = new Paint();
	private Paint paintPlayerChipCircleSelect = new Paint();
	private Paint paintPlayerChipText = new Paint();
	private Paint paintTouchPoint = new Paint();
	private PlayerChipHelper playerChipHelper = new PlayerChipHelper();
	private PlayerChip playerChipGrab;
	private Bitmap bitmapFootball;
	private Matrix matrixFootball = new Matrix();

	public void init(Context context) {
		setBackgroundColor(backGroundColor);
		//
		BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.soccer_ball_05); 
		bitmapFootball = Bitmap.createScaledBitmap(drawable.getBitmap(), 50, 50, true);
		//
		paintPlayerChipText.setColor(Color.rgb(243, 242, 222));
		paintPlayerChipText.setTextSize(40);
		paintPlayerChipText.setStrokeWidth(5f);
		paintPlayerChipText.setTypeface(Typeface.DEFAULT_BOLD);
		//
		paintPlayerChipCircle.setColor(Color.rgb(153, 62, 82));
		paintPlayerChipCircle.setAntiAlias(true);
		//
		paintPlayerChipCircleSelect.setColor(Color.rgb(133, 232, 122));
		paintPlayerChipCircleSelect.setAntiAlias(true);
		//
		paintTouchPoint.setColor(Color.argb(177, 233, 232, 222));
		paintTouchPoint.setAntiAlias(true);

		setBackgroundDrawables();
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void setBackgroundDrawables() {
		if (Build.VERSION.SDK_INT >= 16) {
			setBackground(getResources().getDrawable(R.drawable.bg_stadium_green04));
		} else {
			setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_stadium_green04));
		}
	}

	@Override
	public void addPlayerChip() {
		playerChipHelper.addPlayerChip();
		invalidate();
	}

	@Override
	public void addPlayerChip(int id) {

	}

	@Override
	public void removePlayerChip() {
		playerChipHelper.removePlayerChip();
		invalidate();
	}

	@Override
	public void removePlayerChip(int id) {

	}

	@Override
	public void clearPlayerChips() {
		playerChipHelper.clearPlayerChips();
		invalidate();
	}
	
	public void setTeamFormation(TeamFormation formation) {
		playerChipHelper.setTeamFormation(formation);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawPlayGround(canvas);
		drawPlayerChips(canvas);
		drawFootBall(canvas);
		drawTouchPoint(canvas);
		super.onDraw(canvas);
	}

	private void drawFootBall(Canvas canvas) {
		matrixFootball.reset();
		matrixFootball.preTranslate(viewWidth/2-25, 55);
		canvas.drawBitmap(bitmapFootball, matrixFootball, null);
	}
	
	private void drawPlayGround(Canvas canvas) {

	}

	private void drawTouchPoint(Canvas canvas) {
		if (TouchEvent.DOWN.equals(touchMode) || TouchEvent.MOVE.equals(touchMode)) {
			canvas.drawCircle(touchX, touchY, 25, paintTouchPoint);
		}
	}

	private void drawPlayerChips(Canvas canvas) {
		ArrayList<PlayerChip> playerChipList = playerChipHelper.getPlayerChipList();
		if (playerChipList == null) {
			return;
		}
		// grab chip
		PlayerChip grabChip = playerChipGrab;
		if (grabChip != null && ChipTouchMode.GRAB.equals(chipTouchMode)) {
			// draw outer circle
			int radius = (int) grabChip.length / 2;
			canvas.drawCircle(grabChip.viewPoint.x, grabChip.viewPoint.y, radius + 5, paintPlayerChipCircleSelect);
			// draw inner text (number)
			canvas.drawText(String.valueOf(grabChip.number), grabChip.viewPoint.x - 10, grabChip.viewPoint.y + 10, paintPlayerChipText);
		}
		// non-grab chips
		for (PlayerChip chip : playerChipList) {
			//
			if (chip.grab) {
				continue;
			}
			// draw outer circle
			int radius = (int) chip.length / 2;
			Paint paint = (chip.paint == null) ? paintPlayerChipCircle : chip.paint;
			canvas.drawCircle(chip.viewPoint.x, chip.viewPoint.y, radius, paint);
			// draw inner text (number)
			canvas.drawText(String.valueOf(chip.number), chip.viewPoint.x - 10, chip.viewPoint.y + 10, paintPlayerChipText);
		}
	}

	// grab player chip
	@Override
	@SuppressLint("ClickableViewAccessibility")
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

	private enum TouchEvent {
		NONE, DOWN, MOVE, UP;
	}

	private enum ChipTouchMode {
		NONE, GRAB,
	}

	private TouchEvent touchMode = TouchEvent.NONE;
	private ChipTouchMode chipTouchMode = ChipTouchMode.NONE;
	private float touchX, touchY;

	// private float transX, transY;

	private void handleTouchActionDown(MotionEvent event) {
		// find if it exist
		ArrayList<PlayerChip> playerChipList = playerChipHelper.getPlayerChipList();
		if (playerChipList == null) {
			return;
		}
		if (ChipTouchMode.GRAB.equals(chipTouchMode)) {
			return;
		}
		//
		float curTouchX = getTouchX(event);
		float curTouchY = getTouchY(event);
		//BDLog.d("handleTouchActionDown size:" + playerChipList.size());
		for (PlayerChip chip : playerChipList) {
			if (isGrabPointOnChip(chip, curTouchX, curTouchY)) {
				playerChipGrab = chip;
				playerChipGrab.grab = true;
				chipTouchMode = ChipTouchMode.GRAB;
				BDLog.e("handleTouchActionDown GRAB!!! :" + chip.number);
				break;
			}
		}
		if (ChipTouchMode.NONE.equals(chipTouchMode)) {
			playerChipGrab = null;
		}
		touchX = getTouchX(event);
		touchY = getTouchY(event);
	}
	
	private float getTouchX(MotionEvent event){
		return event.getX() - left;
	}
	private float getTouchY(MotionEvent event){
		return event.getY() - top;
	}

	private boolean isGrabPointOnChip(PlayerChip chip, float touchX, float touchY) {
		Rect rect = new Rect();
		rect.set((int) (chip.viewPoint.x - 10), (int) (chip.viewPoint.y - 10), (int) (chip.viewPoint.x + chip.length + 10), (int) (chip.viewPoint.y + chip.length + 10));
		BDLog.d("isGrabPointOnChip touchX:" + touchX + ",touchY:" + touchY + "// viewX:" + chip.viewPoint.x + ",viewY:" + chip.viewPoint.y + ", num:" + chip.number + ", R.width:"
				+ rect.width() + ", R.height:" + rect.height());
		return rect.contains((int) touchX, (int) touchY);
	}

	private void handleTouchActionMove(MotionEvent event) {
		touchX = getTouchX(event);
		touchY = getTouchY(event);
		if (ChipTouchMode.GRAB.equals(chipTouchMode) && playerChipGrab.grab) {
			playerChipGrab.viewPoint.x = touchX;
			playerChipGrab.viewPoint.y = touchY;
		}
		BDLog.d("handleTouchActionMove chipTouchMode:" + chipTouchMode + ", touchX:" + touchX + ", touchY:" + touchY);
		invalidate();
	}

	private void handleTouchActionUp(MotionEvent event) {
		BDLog.d("handleTouchActionUp playerChipGrab:" + playerChipGrab);
		// set renew player chip
		if (ChipTouchMode.GRAB.equals(chipTouchMode) && playerChipGrab != null) {
			touchX = getTouchX(event);
			touchY = getTouchY(event);
			playerChipGrab.viewPoint.x = touchX;
			playerChipGrab.viewPoint.y = touchY;
			playerChipGrab.grab = false;
			playerChipHelper.renewPlayerChip(playerChipGrab);
		}
		chipTouchMode = ChipTouchMode.NONE;
		invalidate();
	}

	private int left, top, right, bottom;
	private int viewWidth, viewHeight;

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
			playerChipHelper.setViewBounds(viewWidth, viewHeight);
		}
		BDLog.e("onLayout left:" + left + ", top:" + top + ", right:" + right + ", bottom:" + bottom);
		super.onLayout(changed, l, t, r, b);
	}
}
