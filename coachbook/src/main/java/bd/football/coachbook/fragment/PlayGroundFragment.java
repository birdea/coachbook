package bd.football.coachbook.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import bd.football.coachbook.MainActivity.PlaceholderFragment;
import bd.football.coachbook.R;
import bd.football.coachbook.helper.AnimationHelper;
import bd.football.coachbook.helper.ShareHelper;
import bd.football.coachbook.model.PlayerChip;
import bd.football.coachbook.model.TeamFormation;
import bd.football.coachbook.view.PlayGroundView;
import bd.football.coachbook.view.PlayGroundView.OnChipInterface;

public class PlayGroundFragment extends PlaceholderFragment {

	private PlayGroundView playGroundView;
	private LinearLayout layer_bottom_member_info, lly_control_layer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.frag_play_ground, container, false);
		findView(rootView);
		return rootView;
	}

	private void findView(View rootView) {

		lly_control_layer = (LinearLayout) rootView.findViewById(R.id.lly_control_layer);
		lly_control_layer.setVisibility(View.VISIBLE);
		layer_bottom_member_info = (LinearLayout) rootView.findViewById(R.id.layer_bottom_member_info);
		layer_bottom_member_info.setVisibility(View.INVISIBLE);

		playGroundView = (PlayGroundView) rootView.findViewById(R.id.playGroundView);
		playGroundView.setOnChipInterface(new OnChipInterface() {
			@Override
			public void grabOn(PlayerChip grab) {
				handleGrabOn(grab);
			}

			@Override
			public void grabOff() {
				handleGrabOff();
			}
		});
		rootView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return playGroundView.dispatchTouchEvent(event);
			}
		});
		
		Button btn_save_image = (Button) rootView.findViewById(R.id.btn_save_image);
		btn_save_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveGroundImage();
			}
		});

		Button btn_change_left = (Button) rootView.findViewById(R.id.btn_change_left);
		btn_change_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickLeft();
			}
		});
		Button btn_change_right = (Button) rootView.findViewById(R.id.btn_change_right);
		btn_change_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickRight();
			}
		});
		initSpinnerFormation(rootView);
	}
	
	private void saveGroundImage(){
		playGroundView.buildDrawingCache();
		Bitmap bmap = playGroundView.getDrawingCache();
		ShareHelper.setGroundBitmap(bmap);
		playGroundView.destroyDrawingCache();
	}
	
	private void handleGrabOn(PlayerChip grab) {
		setGrabPlayerInfo(grab);
		AnimationHelper.startTranslateAnimation(layer_bottom_member_info, true, null, true);
		/*AnimationHelper.switchAlphaAnimation(layer_bottom_member_info, 0, 100, 1500, new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				layer_bottom_member_info.setVisibility(View.VISIBLE);
				lly_control_layer.setVisibility(View.INVISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				
			}
		});*/
	}
	
	private void handleGrabOff() {
		AnimationHelper.startTranslateAnimation(layer_bottom_member_info, false, null, true);
		/*AnimationHelper.switchAlphaAnimation(layer_bottom_member_info, 100, 0, 1500, new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				layer_bottom_member_info.setVisibility(View.INVISIBLE);
				lly_control_layer.setVisibility(View.VISIBLE);
			}
		});*/
	}

	private void setGrabPlayerInfo(PlayerChip grab) {
		// layer_bottom_member_info
		View view = layer_bottom_member_info;
		ImageView iv_member_photo = (ImageView) view.findViewById(R.id.iv_member_photo);
		TextView tv_member_name = (TextView) view.findViewById(R.id.tv_member_name);
		TextView tv_member_age = (TextView) view.findViewById(R.id.tv_member_age);
		TextView tv_member_phone = (TextView) view.findViewById(R.id.tv_member_phone);
		//
		iv_member_photo.setImageBitmap(grab.photo);
		tv_member_name.setText(String.valueOf(grab.name));
		tv_member_age.setText(String.valueOf(grab.age));
		tv_member_phone.setText(String.valueOf(grab.number));
	}

	private int preBackgroundResourcIndex = 2;

	private void clickLeft() {
		preBackgroundResourcIndex--;
		if (preBackgroundResourcIndex < 0) {
			preBackgroundResourcIndex = 3;
		}
		playGroundView.setBackgroundResource(getResource());
	}

	private int getResource() {
		switch (preBackgroundResourcIndex) {
		case 0:
			return R.drawable.bg_stadium_green01;
		case 1:
			return R.drawable.bg_stadium_green02;
		case 2:
			return R.drawable.bg_stadium_green03;
		case 3:
			return R.drawable.bg_stadium_green04;
		default:
			return R.drawable.bg_stadium_green03;
		}
	}

	private void clickRight() {
		preBackgroundResourcIndex++;
		if (preBackgroundResourcIndex > 3) {
			preBackgroundResourcIndex = 0;
		}
		playGroundView.setBackgroundResource(getResource());
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
