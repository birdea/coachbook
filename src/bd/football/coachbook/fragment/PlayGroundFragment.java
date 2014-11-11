package bd.football.coachbook.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import bd.football.coachbook.MainActivity.PlaceholderFragment;
import bd.football.coachbook.R;
import bd.football.coachbook.model.TeamFormation;
import bd.football.coachbook.view.PlayGroundView;

public class PlayGroundFragment extends PlaceholderFragment {

	private PlayGroundView playGroundView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_play_ground, container, false);
		findView(rootView);
		return rootView;
	}

	private void findView(View rootView) {
		playGroundView = (PlayGroundView) rootView.findViewById(R.id.playGroundView);
		rootView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return playGroundView.dispatchTouchEvent(event);
			}
		});
		initSpinnerFormation(rootView);
	}

	public void onClickAddPlayer(View view) {
		playGroundView.addPlayerChip();
	}

	public void onClickRemovePlayer(View view) {
		playGroundView.removePlayerChip();
	}

	public void onClickClearPlayer(View view) {
		playGroundView.clearPlayerChips();
	}

	private void initSpinnerFormation(View rootView) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, TeamFormation.getFormationNames());
		Spinner sp = (Spinner) rootView.findViewById(R.id.spinner_formation);
		sp.setPrompt("Set Formation");
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				playGroundView.setTeamFormation(TeamFormation.getFormationOrdinally(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
}
