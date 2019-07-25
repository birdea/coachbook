package bd.football.coachbook;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import bd.football.coachbook.fragment.NavigationDrawerFragment;
import bd.football.coachbook.fragment.PlayGroundFragment;
import bd.football.coachbook.fragment.PlayScheduleFragment;
import bd.football.coachbook.fragment.PlayTimeRecordFragment;
import bd.football.coachbook.fragment.PlayerMemberListFragment;
import bd.football.coachbook.fragment.PlayerRegisterFragment;
import bd.football.coachbook.utils.BLog;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}

	private PlaceholderFragment placeholderFragment;
	
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		placeholderFragment = PlaceholderFragment.newInstance(position + 1);
		fragmentManager.beginTransaction().replace(R.id.container, placeholderFragment).commit();
	}
	
	public enum SectionViewType{
		MEMBER_LIST(1, "MEMBER LIST"),
		MEMBER_REG(2, "MEMBER REG"),
		PLAY_GROUND(3, "PLAY GROUND"),
		PLAY_LIVE(4, "PLAY LIVE"),
		//HISTORY(5, "HISTORY"),
		//TRAIN(6, "TRAIN"),
		SCHEDULE(7, "SCHEDULE"),
		//MATCH(8, "MATCH"),
		//MEMBER_MONEY(9, "MONEY $"),
		//SETTING(10, "SET"),
		//HELP(11, "HELP"),
		;
		
		
		public int index;
		public String title;
		
		SectionViewType(int index, String title){
			this.index = index;
			this.title = title;
		}
		
		public static String[] getTitles(){
			ArrayList<String> list = new ArrayList<String>();
			for(SectionViewType section : SectionViewType.values()){
				list.add(section.title);
			}
			return list.toArray(new String[list.size()]);
		}
		
	}

	public void onSectionAttached(int number) {
		for(SectionViewType type :  SectionViewType.values()){
			if(type.index == number){
				mTitle = type.title;
			}
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = getSectionFragment(sectionNumber);
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			BLog.e("newInstance - sectionNumber:" + sectionNumber);
			return fragment;
		}

		public static PlaceholderFragment getSectionFragment(int sectionNumber) {
			switch (sectionNumber) {
			case 1:
				return new PlayerMemberListFragment();
			case 2:
				return new PlayerRegisterFragment();
			case 3:
				return new PlayGroundFragment();
			case 4:
				return new PlayTimeRecordFragment();
			case 5:
				return new PlayScheduleFragment();
			default:
				return new PlaceholderFragment();
			}
		}
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}
	
	private long mLastBackPressed = 0;
	
	@Override
	public void onBackPressed() {
		long time = System.currentTimeMillis();
		if(mLastBackPressed == 0 || time - mLastBackPressed > 2000){
			mLastBackPressed = time;
			Toast.makeText(getApplicationContext(), "PRESS AGAIN TO QUIT", Toast.LENGTH_SHORT).show();
			return;
		}
		super.onBackPressed();
	}

}
