package bd.football.coachbook.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import bd.football.coachbook.R;
import bd.football.coachbook.model.TeamFormation;
import bd.football.coachbook.view.PlayGroundView;

public class PlayGroundActivity extends Activity {

	private PlayGroundView playGroundView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frag_play_ground);

		findView();
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

	private void findView() {
		playGroundView = (PlayGroundView) findViewById(R.id.playGroundView);
		initSpinnerFormation();
	}

	private void initSpinnerFormation() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item,
				TeamFormation.getFormationNames());
		Spinner sp = (Spinner) this.findViewById(R.id.spinner_formation);
		sp.setPrompt("Set Formation");
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				playGroundView.setTeamFormation(TeamFormation
						.getFormationOrdinally(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// BDLog.w("onTouchEvent on activity:" + event);
		return playGroundView.dispatchTouchEvent(event);
		// return super.onTouchEvent(event);
	}
}
