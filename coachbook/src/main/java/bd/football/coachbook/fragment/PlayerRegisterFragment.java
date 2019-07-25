package bd.football.coachbook.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import bd.football.coachbook.MainActivity.PlaceholderFragment;
import bd.football.coachbook.R;
import bd.football.coachbook.context.Storage;
import bd.football.coachbook.db.DBControler;
import bd.football.coachbook.db.dao.MemberScheme.MemberParam;
import bd.football.coachbook.fragment.dialog.ContactsChoiseDialog;
import bd.football.coachbook.helper.ContactsHelper;
import bd.football.coachbook.helper.ExifHelper;
import bd.football.coachbook.helper.HandyAsyncCommand;
import bd.football.coachbook.helper.HandyAsyncTask;
import bd.football.coachbook.helper.PhotoRegisterHelper;
import bd.football.coachbook.utils.BLog;

public class PlayerRegisterFragment extends PlaceholderFragment {

	private View rootView;
	private ImageView iv_reg_player_photo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.frag_player_member_register, container, false);
		findView(rootView);
		this.rootView = rootView;
		return rootView;
	}

	private void findView(View rootView) {
		HandyAsyncTask task = new HandyAsyncTask(new HandyAsyncCommand() {
			@Override
			public void onResult(boolean result, Exception e) {
			}
			@Override
			public boolean executeExceptionSafely() throws Exception {
				contacts = ContactsHelper.getContactList(getActivity());
				return true;
			}
		});
		task.execute();

		iv_reg_player_photo = (ImageView) rootView.findViewById(R.id.iv_reg_player_photo);

		Button btn_reg_player_photo_camera = (Button) rootView.findViewById(R.id.btn_reg_player_photo_camera);
		btn_reg_player_photo_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickCameraTakePhoto();
			}
		});
		Button btn_reg_player_photo_gallery = (Button) rootView.findViewById(R.id.btn_reg_player_photo_gallery);
		btn_reg_player_photo_gallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickCameraTakeGallery();
			}
		});

		Button btn_add_player = (Button) rootView.findViewById(R.id.btn_add_player);
		btn_add_player.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickAddPlayer();
			}
		});

		Button btn_reg_player_contacts = (Button) rootView.findViewById(R.id.btn_reg_player_contacts);
		btn_reg_player_contacts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickReadContacts();
			}
		});
	}
	private ArrayList<MemberParam> contacts;
	private void onClickReadContacts() {
		HandyAsyncTask task = new HandyAsyncTask(new HandyAsyncCommand() {
			@Override
			public void onResult(boolean result, Exception e) {
				ContactsChoiseDialog contactsChoiseDialog = new ContactsChoiseDialog(getActivity(), contacts, new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						BLog.e("onItemClick - position:" + position);
						setSelectedContact(contacts.get(position));
					}
				});
				contactsChoiseDialog.show();
			}
			@Override
			public boolean executeExceptionSafely() throws Exception {
				contacts = ContactsHelper.getContactList(getActivity());
				return true;
			}
		});
		task.execute();
	}

	private void setSelectedContact(MemberParam member) {
		EditText et_player_name = (EditText) rootView.findViewById(R.id.et_player_name);
		EditText et_player_phone = (EditText) rootView.findViewById(R.id.et_player_phone);
		//
		et_player_name.setText(String.valueOf(member.name));
		et_player_phone.setText(String.valueOf(member.phone_number));
		//
		if (member.photo_id != null) {
			Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, Long.parseLong(member.photo_id));
			handleContactPhoto(photoUri);
		}
	}

	private void onClickAddPlayer() {

		EditText et_player_name = (EditText) rootView.findViewById(R.id.et_player_name);
		EditText et_player_age = (EditText) rootView.findViewById(R.id.et_player_age);
		EditText et_player_phone = (EditText) rootView.findViewById(R.id.et_player_phone);
		EditText et_player_email = (EditText) rootView.findViewById(R.id.et_player_email);
		EditText et_player_number = (EditText) rootView.findViewById(R.id.et_player_number);

		MemberParam new_player = new MemberParam();
		try {
			new_player.name = et_player_name.getText().toString();
		} catch (Exception e) {
		}
		try {
			new_player.age = Integer.parseInt(et_player_age.getText().toString());
		} catch (Exception e) {
			new_player.age = 0;
		}
		try {
			new_player.phone_number = et_player_phone.getText().toString();
		} catch (Exception e) {

		}
		try {
			new_player.email = et_player_email.getText().toString();
		} catch (Exception e) {

		}
		try {
			new_player.play_number = Integer.parseInt(et_player_number.getText().toString());
		} catch (Exception e) {
			new_player.play_number = 0;
		}
		try {
			new_player.photo_uri = mThumbPhotoPath;
		} catch (Exception e) {

		}

		DBControler.getDBContainer().mMemberDao.addMember(new_player);
	}

	private void onClickRemovePlayer() {

	}

	private String mCurrentPhotoPath;
	private String mThumbPhotoPath;

	private static final int REQ_CAMERA_TAKE_PHOTO = 0x0123321;
	private static final int REQ_GALERY_TAKE_PHOTO = 0x0123322;

	private void onClickCameraTakePhoto() {
		if (PhotoRegisterHelper.isIntentAvailable(getActivity(), MediaStore.ACTION_IMAGE_CAPTURE)) {
			dispatchTakePictureIntent();
		}
	}

	private void onClickCameraTakeGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQ_GALERY_TAKE_PHOTO);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQ_CAMERA_TAKE_PHOTO) {
			handleCameraPhoto(data);
		} else if (requestCode == REQ_GALERY_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
			handleGalleryPhoto(data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void handleContactPhoto(Uri uri) {
		/*
		 * String[] filePathColumn = { MediaStore.Images.Media.DATA }; Cursor
		 * imgCursor = getActivity().getContentResolver().query(uri,
		 * filePathColumn, null, null, null); imgCursor.moveToFirst();
		 * 
		 * int columnIdx = imgCursor.getColumnIndex(filePathColumn[0]); String
		 * imgPath = imgCursor.getString(columnIdx);
		 */

		Bitmap bitmap = null;
		try {
			bitmap = Images.Media.getBitmap(getActivity().getContentResolver(), uri);
			handlePhotoBitmap(bitmap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Bitmap bitmap = ExifHelper.getRotateBitmapIfNecessary(imgPath);
	}

	private void handleGalleryPhoto(Intent data) {
		if (data == null) {
			return;
		}
		Uri uri = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor imgCursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
		imgCursor.moveToFirst();

		int columnIdx = imgCursor.getColumnIndex(filePathColumn[0]);
		String imgPath = imgCursor.getString(columnIdx);
		Bitmap bitmap = ExifHelper.getRotateBitmapIfNecessary(imgPath);
		handlePhotoBitmap(bitmap);
	}

	private void handleCameraPhoto(Intent intent) {
		if(intent == null) {
			return;
		}
		Bundle extras = intent.getExtras();
		handlePhotoBitmap((Bitmap) extras.get("data"));
	}

	private void handlePhotoBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return;
		}
		BLog.e("handleSmallCameraPhoto bitmap width:" + bitmap.getWidth() + ", height:" + bitmap.getHeight());
		bitmap = getEffectiveBitmap(bitmap);
		// make name of it
		String cacheDir = Storage.getExternalCacheDir(getActivity()).getAbsolutePath();
		mThumbPhotoPath = String.format("%s/%s.jpg", cacheDir, sdf.format(new Date(System.currentTimeMillis())));
		BLog.e("handleSmallCameraPhoto mThumbPhotoPath:" + mThumbPhotoPath);
		// save the bitmap file on cache folder
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(mThumbPhotoPath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		iv_reg_player_photo.setImageBitmap(bitmap);
	}

	private static final int MAX_BITMAP_LENGTH = 250;

	private Bitmap getEffectiveBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth(), height = bitmap.getHeight();
		if (width <= MAX_BITMAP_LENGTH && height <= MAX_BITMAP_LENGTH) {
			return bitmap;
		}
		Bitmap scaled = Bitmap.createScaledBitmap(bitmap, MAX_BITMAP_LENGTH, MAX_BITMAP_LENGTH, false);
		bitmap.recycle();
		return scaled;
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (PhotoRegisterHelper.isIntentAvailable(getActivity().getApplicationContext(), MediaStore.ACTION_IMAGE_CAPTURE)) {
			/*
			 * File f = createImageFile(); mCurrentPhotoPath =
			 * f.getAbsolutePath();
			 * takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
			 * Uri.fromFile(f));
			 */
			startActivityForResult(takePictureIntent, REQ_CAMERA_TAKE_PHOTO);
		}
	}

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhMMss", Locale.getDefault());

	private File createImageFile() {
		String savePath = "/mnt/sdcard/";
		String fullPath = savePath + "cb_" + sdf.format(new Date(System.currentTimeMillis())) + ".jpg";
		File file = new File(fullPath);
		return file;
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		getActivity().sendBroadcast(mediaScanIntent);
	}

}
