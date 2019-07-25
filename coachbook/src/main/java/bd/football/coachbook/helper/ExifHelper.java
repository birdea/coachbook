package bd.football.coachbook.helper;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import bd.football.coachbook.utils.BLog;

public class ExifHelper {

	public synchronized static Bitmap getRotateBitmapIfNecessary(String filepath) {
		Bitmap org = BitmapFactory.decodeFile(filepath); 
		int degrees = getPhotoOrientationDegree(filepath);
		if (0 != degrees) {
			return getRotatedBitmap(org, degrees);
		}
		return org;
	}

	public synchronized static int getPhotoOrientationDegree(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			}
		} catch (IOException e) {
			BLog.d("Error: " + e.getMessage());
		}
		BLog.d("Photo Degree: " + degree);
		return degree;
	}

	/**
	 * 이미지를 특정 각도로 회전
	 * 
	 * @param bitmap
	 * @param degrees
	 * @return
	 */
	public synchronized static Bitmap getRotatedBitmap(Bitmap bitmap, int degrees) {
		if (degrees != 0 && bitmap != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
				if (bitmap != b2) {
					bitmap.recycle();
					bitmap = b2;
				}
			} catch (OutOfMemoryError e) {
				BLog.d("Error: " + e.getMessage());
			}
		}
		return bitmap;
	}
}
