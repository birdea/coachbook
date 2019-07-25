package bd.football.coachbook.fragment.dialog;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import bd.football.coachbook.R;
import bd.football.coachbook.db.dao.MemberScheme.MemberParam;

public class MatchRecordDialog extends Dialog {

	public MatchRecordDialog(Context context, ArrayList<MemberParam> list, OnItemClickListener onItemClickListener) {
		super(context);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.diag_reg_contacts);
		// - findView
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setText(String.valueOf("Select members"));
		//
		Button cancelButton = (Button) findViewById(R.id.btn_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		//
		ListView lv_contacts = (ListView) findViewById(R.id.lv_contacts);
		ContactsListAdapter contactsListAdapter = new ContactsListAdapter(context, list);
		lv_contacts.setAdapter(contactsListAdapter);
		lv_contacts.setOnItemClickListener(onItemClickListener);
	}

	private class ContactsListAdapter extends BaseAdapter {

		private ArrayList<MemberParam> list;// =
											// DBControler.getDBContainer().mMemberDao.getPlayMembers();
		private Context context;
		private Drawable defaultImage;

		private class ViewHolder {
			TextView name, age, phone, email;
			ImageView photo;
		}

		public ContactsListAdapter(Context context, ArrayList<MemberParam> list) {
			this.context = context;
			this.list = list;
			defaultImage = context.getResources().getDrawable(R.drawable.soccer_ball_08);
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
			if (list != null) {
				return list.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder holder = null;
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
			MemberParam param = list.get(position);
			if (param.photo_id != null) {
				try {
					Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, Long.parseLong(param.photo_id));
					holder.photo.setImageURI(photoUri);
				} catch (Exception e) {
					e.printStackTrace();
					holder.photo.setImageDrawable(defaultImage);
				}
			} else {
				holder.photo.setImageDrawable(defaultImage);
			}
			holder.name.setText(String.valueOf(param.name));
			holder.age.setText(String.valueOf(param.age));
			holder.phone.setText(String.valueOf(param.phone_number));
			return view;
		}
	}
}
