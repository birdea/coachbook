package bd.football.coachbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bd.football.coachbook.MainActivity.PlaceholderFragment;
import bd.football.coachbook.R;

public class PlayerRegisterFragment extends PlaceholderFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_player_register, container, false);
		findView();
		return rootView;
	}

	private void findView() {

	}
}
