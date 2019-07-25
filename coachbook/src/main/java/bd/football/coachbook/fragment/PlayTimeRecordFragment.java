package bd.football.coachbook.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import bd.football.coachbook.MainActivity.PlaceholderFragment;
import bd.football.coachbook.R;
import bd.football.coachbook.db.DBControler;
import bd.football.coachbook.db.dao.MemberScheme.MemberParam;
import bd.football.coachbook.fragment.dialog.MatchRecordDialog;
import bd.football.coachbook.sound.Beeper;
import bd.football.coachbook.utils.BLog;
import bd.football.coachbook.view.PlayTimerView;
import bd.football.coachbook.view.PlayTimerView.OnCountTimerListener;

public class PlayTimeRecordFragment extends PlaceholderFragment {

	private PlayTimerView playTimerView;
	private MatchRecordListAdapter matchRecordListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.frag_play_time_record, container, false);
		findView(rootView);
		return rootView;
	}

	private void findView(View rootView) {

		playTimerView = (PlayTimerView) rootView.findViewById(R.id.playTimerView);

		rootView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return playTimerView.dispatchTouchEvent(event);
			}
		});
		Beeper.init(getActivity());

		playTimerView.setOnCountTimerListener(new OnCountTimerListener() {
			@Override
			public void onStop() {
				Beeper.getInstance().play(R.raw.beep_alarm);
			}

			@Override
			public void onStart() {
				Beeper.getInstance().stop();
			}
		});
		Button btn_timer_mode_change = (Button) rootView.findViewById(R.id.btn_timer_mode_change);
		btn_timer_mode_change.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Beeper.getInstance().stop();
				onClickTimerModeChange(v);
			}
		});

		Button btn_timer_start = (Button) rootView.findViewById(R.id.btn_timer_start);
		btn_timer_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Beeper.getInstance().stop();
				onClickTimerStart(v);
			}
		});
		Button btn_timer_stop = (Button) rootView.findViewById(R.id.btn_timer_stop);
		btn_timer_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Beeper.getInstance().stop();
				onClickTimerStop(v);
			}
		});
		Button btn_goal_team_a = (Button) rootView.findViewById(R.id.btn_goal_team_a);
		btn_goal_team_a.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickRecordGoal(true);
			}
		});
		Button btn_goal_team_b = (Button) rootView.findViewById(R.id.btn_goal_team_b);
		btn_goal_team_b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickRecordGoal(false);
			}
		});

		tv_team_a_score = (TextView) rootView.findViewById(R.id.tv_team_a_score);
		tv_team_b_score = (TextView) rootView.findViewById(R.id.tv_team_b_score);
		
		ListView lv_score_in_time = (ListView) rootView.findViewById(R.id.lv_score_in_time);
		matchRecordListAdapter = new MatchRecordListAdapter(getActivity());
		lv_score_in_time.setAdapter(matchRecordListAdapter);
	}

	public void onClickRecordGoal(boolean isTeamLeft) {
		if (isTeamLeft) {
			final ArrayList<MemberParam> list = DBControler.getDBContainer().mMemberDao.getPlayMembers();
			MatchRecordDialog matchRecordDialog = new MatchRecordDialog(getActivity(), list, new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					BLog.d("onClickRecordGoal position:" + position);
					MemberParam member = list.get(position);
					MatchRecord record = new MatchRecord();
					record.teamLeft = true;
					record.name = member.name;
					record.photo_uri = member.photo_uri;
					record.strRecordTime = playTimerView.getLastMinSecValue();
					matchRecordListAdapter.addItem(record);
					matchRecordListAdapter.notifyDataSetChanged();
					refreshScoreBoard();
				}
			});
			matchRecordDialog.show();
		} else {
			MatchRecord record = new MatchRecord();
			record.teamLeft = false;
			record.name = "Unknown";
			record.photo_uri = "";
			record.strRecordTime = playTimerView.getLastMinSecValue();
			matchRecordListAdapter.addItem(record);
			matchRecordListAdapter.notifyDataSetChanged();
			refreshScoreBoard();
		}
	}
	
	TextView tv_team_a_score, tv_team_b_score;
	
	private void refreshScoreBoard() {
		int score_a = matchRecordListAdapter.getTeamScore(true);
		int score_b = matchRecordListAdapter.getTeamScore(false);
		tv_team_a_score.setText(String.valueOf(score_a));
		tv_team_b_score.setText(String.valueOf(score_b));
	}
	
	public void onClickTimerModeChange(View view) {
		playTimerView.changeMode();
		playTimerView.stopTimer();
	}

	public void onClickTimerStart(View view) {
		int cdTimeSec = 100;
		try {
			cdTimeSec = Integer.parseInt(((EditText) getActivity().findViewById(R.id.et_interval)).getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		playTimerView.setCountDownTime(cdTimeSec);
		playTimerView.setTimerUpdateInterval(10);
		playTimerView.startTimer();
	}

	public void onClickTimerStop(View view) {
		playTimerView.stopTimer();
	}

	public class MatchRecord {
		boolean teamLeft;
		String strRecordTime;
		String name;
		String photo_uri;
	}

	private static class MatchRecordListAdapter extends BaseAdapter {

		Context context;
		ArrayList<MatchRecord> list;

		private class ViewHolder {
			TextView name_a, name_b, record_time;
			ImageView photo_a, photo_b;
			View lly_team_a_record, lly_team_b_record;
		}

		public MatchRecordListAdapter(Context context) {
			BLog.d("MemberListAdapter created: " + list);
			this.context = context;
			this.list = new ArrayList<MatchRecord>();
		}

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}

		@Override
		public MatchRecord getItem(int position) {
			if (list != null && position < list.size()) {
				return list.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void addItem(MatchRecord record) {
			if (list != null) {
				list.add(record);
			}
		}
		
		public int getTeamScore(boolean isTeamLeft){
			int score = 0;
			if (list != null) {
				for(MatchRecord record : list) {
					if(record.teamLeft == isTeamLeft){
						score++;
					}
				}
			}
			return score;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder holder = null;
			BLog.d("getView " + position + "/ view:" + view);
			if (view == null || view.getTag() == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = (ViewGroup) inflater.inflate(R.layout.adapt_match_record_list_item, null);
				// create holder
				holder = new ViewHolder();
				// findView
				holder.photo_a = (ImageView) view.findViewById(R.id.iv_member_a_photo);
				holder.name_a = (TextView) view.findViewById(R.id.tv_member_a_name);
				holder.photo_b = (ImageView) view.findViewById(R.id.iv_member_b_photo);
				holder.name_b = (TextView) view.findViewById(R.id.tv_member_b_name);
				holder.record_time = (TextView) view.findViewById(R.id.tv_record_time);
				holder.lly_team_a_record = view.findViewById(R.id.lly_team_a_record);
				holder.lly_team_b_record = view.findViewById(R.id.lly_team_b_record);
				// register contents
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			MatchRecord param = list.get(position);
			//
			holder.record_time.setText(String.valueOf(param.strRecordTime));
			//
			if (param.teamLeft) {
				holder.lly_team_a_record.setVisibility(View.VISIBLE);
				holder.lly_team_b_record.setVisibility(View.INVISIBLE);
				try {
					holder.photo_a.setImageBitmap(BitmapFactory.decodeFile(param.photo_uri));
				} catch (Exception e) {
					e.printStackTrace();
				}
				holder.name_a.setText(String.valueOf(param.name));
			} else {
				holder.lly_team_a_record.setVisibility(View.INVISIBLE);
				holder.lly_team_b_record.setVisibility(View.VISIBLE);
				try {
					holder.photo_b.setImageBitmap(BitmapFactory.decodeFile(param.photo_uri));
				} catch (Exception e) {
					e.printStackTrace();
				}
				holder.name_b.setText(String.valueOf(param.name));
			}
			return view;
		}
	}
}
