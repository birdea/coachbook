package bd.football.coachbook.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import bd.football.coachbook.MainActivity.PlaceholderFragment;
import bd.football.coachbook.R;
import bd.football.coachbook.db.DBControler;
import bd.football.coachbook.db.dao.MemberScheme.MemberParam;
import bd.football.coachbook.utils.BLog;

public class PlayerMemberListFragment extends PlaceholderFragment {

	private ListView lv_member_list;
	private MemberListAdapter mMemberListAdapter;
	private int mlistSelectedItem = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.frag_player_member_list, container, false);
		findView(rootView);
		return rootView;
	}

	private void findView(View rootView) {
		//
		ArrayList<MemberParam> list = DBControler.getDBContainer().mMemberDao.getPlayMembers();
		mMemberListAdapter = new MemberListAdapter(getActivity(), list);
		lv_member_list = (ListView) rootView.findViewById(R.id.lv_member_list);
		lv_member_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BLog.e("onItemClick position:"+position);
				mlistSelectedItem = position;
			}
		});
		lv_member_list.setAdapter(mMemberListAdapter);
		lv_member_list.invalidate();
		//
		Button btn_refresh_player = (Button) rootView.findViewById(R.id.btn_refresh_player);
		Button btn_remove_player = (Button) rootView.findViewById(R.id.btn_remove_player);
		//
		btn_refresh_player.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickRefreshMembers();
			}
		});
		btn_remove_player.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickRemovePlayer();
			}
		});
	}
	
	private void onClickRefreshMembers() {
		ArrayList<MemberParam> list = DBControler.getDBContainer().mMemberDao.getPlayMembers();
		BLog.d("onClickRefreshMembers setList: "+list);
		mMemberListAdapter.setList(list);
		mMemberListAdapter.notifyDataSetChanged();
	}

	private void onClickRemovePlayer() {
		if(mlistSelectedItem > -1 && mlistSelectedItem < mMemberListAdapter.getCount()){
			MemberParam param = mMemberListAdapter.getItem(mlistSelectedItem);
			if(DBControler.getDBContainer().mMemberDao.removeMember(param)){
				mMemberListAdapter.removeItem(mlistSelectedItem);
				mMemberListAdapter.notifyDataSetChanged();
			}
		}
	}

	private class MemberListAdapter extends BaseAdapter {

		Context context;
		ArrayList<MemberParam> list;

		private class ViewHolder {
			TextView name, age, phone, email;
			ImageView photo;
		}

		public MemberListAdapter(Context context, ArrayList<MemberParam> list) {
			BLog.d("MemberListAdapter created: "+list);
			this.context = context;
			this.list = list;
		}

		public void setList(ArrayList<MemberParam> list) {
			BLog.d("MemberListAdapter setList: "+list);
			this.list = list;
		}

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}

		@Override
		public MemberParam getItem(int position) {
			if (list != null && position < list.size()) {
				return list.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public void removeItem(int position){
			if (list != null && position < list.size()) {
				list.remove(position);
			}
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder holder = null;
			BLog.d("getView "+position + "/ view:"+view);
			if (view == null || view.getTag() == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = (ViewGroup) inflater.inflate(R.layout.adapt_member_list_item, null);
				// create holder
				holder = new ViewHolder();
				// findView
				holder.photo = (ImageView) view.findViewById(R.id.iv_member_photo);
				holder.name = (TextView) view.findViewById(R.id.tv_member_name);
				holder.age = (TextView) view.findViewById(R.id.tv_member_age);
				holder.phone = (TextView) view.findViewById(R.id.tv_member_phone);
				// register contents
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			try {
				MemberParam param = list.get(position);
				holder.photo.setImageBitmap(BitmapFactory.decodeFile(param.photo_uri));
				holder.name.setText(String.valueOf(param.name));
				holder.age.setText(String.valueOf(param.age));
				holder.phone.setText(String.valueOf(param.phone_number));
			}catch(Exception e){
				e.printStackTrace();
			}
			return view;
		}
	}
}
