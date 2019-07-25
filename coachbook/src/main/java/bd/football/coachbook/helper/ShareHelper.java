package bd.football.coachbook.helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import bd.football.coachbook.context.Storage;

public class ShareHelper {

	public static final int REQ_SHARE_COACH_FORMATION = 0x12131;
	private static final String CACHE_FILE_NAME_PREFIX = "cb_share_";
	private static final String CACHE_FILE_NAME_EXT = ".jpg";
	private static final String CACHE_FOLDER_NAME = "/share/";

	private String cacheFilePath;

	public void share(Activity owner) {
		if (hasValidGroundBitmap() && saveToInternal(owner) && saveToExternalAppFolder(cacheFilePath)) {
			startCommonShareIntent(owner);
			Toast.makeText(owner, "Share is available.", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(owner, "Share is not available.", Toast.LENGTH_SHORT).show();
		}
	}

	private void startCommonShareIntent(Activity owner) {
		Uri uri = Uri.fromFile(new File(cacheFilePath));
		String type = "image/*";
		String title = "Share the formation";
		String caption = "#shared with coachbook#";
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType(type);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		if (caption != null && caption.equals("") == false) {
			intent.putExtra(Intent.EXTRA_TEXT, caption);
		}
		owner.startActivityForResult(Intent.createChooser(intent, title), REQ_SHARE_COACH_FORMATION);
	}

	private static final String SAVE_DIR_PATH_FOR_ANOTHER_APPS = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Coachbook/.tmp/";

	private boolean saveToInternal(final Context context) {
		try {
			File rootsd = Storage.getExternalCacheDir(context);
			File folder = new File(rootsd.getAbsolutePath() + CACHE_FOLDER_NAME);
			folder.mkdirs();
			removeFiles(folder.getAbsolutePath());
			File f = new File(folder.getAbsolutePath(), CACHE_FILE_NAME_PREFIX + System.currentTimeMillis() + CACHE_FILE_NAME_EXT);
			f.createNewFile();
			cacheFilePath = f.getAbsolutePath();
			Bitmap bitmap = getCurrentBitmap();
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void removeFiles(String dirPath) {
		File dir = new File(dirPath);
		String[] children = dir.list();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				String filename = children[i];
				File f = new File(dirPath + filename);
				if (f.exists()) {
					f.delete();
				}
			}
		}
	}

	private boolean saveToExternalAppFolder(String fromPath) {
		/*File saveDir = new File(SAVE_DIR_PATH_FOR_ANOTHER_APPS);
		saveDir.mkdirs();
		removeFiles(SAVE_DIR_PATH_FOR_ANOTHER_APPS);
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH);
		String destPath = SAVE_DIR_PATH_FOR_ANOTHER_APPS + "coach_share_" + date.format(new Date()) + ".jpg";
		if (fromPath == null || destPath == null) {
			return false;
		}

		if (StringUtils.isBlank(fromPath) || StringUtils.isBlank(destPath)) {
			return false;
		}

		FileInputStream inputStream = null;
		FileChannel inputChannel = null;

		FileOutputStream outputStream = null;
		FileChannel outputChannel = null;
		try {
			inputStream = new FileInputStream(fromPath);
			inputChannel = inputStream.getChannel();

			outputStream = new FileOutputStream(destPath);
			outputChannel = outputStream.getChannel();

			long size = inputChannel.size();

			inputChannel.transferTo(0, size, outputChannel);
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
			}
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
			}
			try {
				if (inputChannel != null) {
					inputChannel.close();
				}
			} catch (IOException e) {
			}
			try {
				if (outputChannel != null) {
					outputChannel.close();
				}
			} catch (IOException e) {
			}
		}
		 */
		return true;
	}

	private static Bitmap mGroundBitmap;

	public static void setGroundBitmap(Bitmap bitmap) {
		if (bitmap == null || bitmap.isRecycled()) {
			return;
		}
		mGroundBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);// Bitmap.createBitmap(bitmap.,
																	// bitmap.getWidth(),
																	// bitmap.getHeight(),
																	// Bitmap.Config.ARGB_8888);
	}

	private Bitmap getCurrentBitmap() {
		return mGroundBitmap;
	}

	private boolean hasValidGroundBitmap() {
		if (mGroundBitmap == null || mGroundBitmap.isRecycled()) {
			return false;
		}
		return true;
	}
}
