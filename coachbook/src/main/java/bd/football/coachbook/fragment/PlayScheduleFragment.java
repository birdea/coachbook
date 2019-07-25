package bd.football.coachbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bd.football.coachbook.MainActivity.PlaceholderFragment;
import bd.football.coachbook.R;

public class PlayScheduleFragment extends PlaceholderFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.frag_play_schedule, container, false);
		findView(rootView);
		return rootView;
	}

	private void findView(View rootView) {

	}
}
