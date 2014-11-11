package bd.football.coachbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import bd.football.coachbook.MainActivity.PlaceholderFragment;
import bd.football.coachbook.R;
import bd.football.coachbook.view.PlayTimerView;

public class PlayTimeRecordFragment extends PlaceholderFragment {

	private PlayTimerView playTimerView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_play_time_record, container, false);
		findView(rootView);
		return rootView;
	}

	private void findView(View rootView) {
		playTimerView = (PlayTimerView) rootView.findViewById(R.id.playTimerView);
	}

	public void onClickTimerModeChange(View view) {
		playTimerView.changeMode();
		playTimerView.stopTimer();
	}

	public void onClickTimerStart(View view) {
		int interval = 100;
		try {
			interval = Integer.parseInt(((EditText) getActivity().findViewById(R.id.et_interval)).getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		playTimerView.setTimerUpdateInterval(interval);
		playTimerView.startTimer();
	}

	public void onClickTimerStop(View view) {
		playTimerView.stopTimer();
	}
}
