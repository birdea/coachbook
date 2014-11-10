package bd.football.coachbook.activity;

import bd.football.coachbook.R;
import bd.football.coachbook.view.PlayTimerView;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class PlayTimeRecordActivity extends Activity{

	private PlayTimerView playTimerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_time_record);
		findView();
	}
	private void findView() {
		playTimerView = (PlayTimerView)findViewById(R.id.playTimerView);
	}
	
	public void onClickTimerModeChange(View view) {
		playTimerView.changeMode();
		playTimerView.stopTimer();
	}
	
	public void onClickTimerStart(View view) {
		int interval = 100;
		try {
			interval = Integer.parseInt(((EditText)findViewById(R.id.et_interval)).getText().toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		playTimerView.setTimerUpdateInterval(interval);
		playTimerView.startTimer();
	}
	
	public void onClickTimerStop(View view) {
		playTimerView.stopTimer();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// BDLog.w("onTouchEvent on activity:" + event);
		return playTimerView.dispatchTouchEvent(event);
	}
}
