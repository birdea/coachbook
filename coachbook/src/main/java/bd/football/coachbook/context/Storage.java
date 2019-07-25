package bd.football.coachbook.context;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class Storage {

	public static File getExternalCacheDir(Context context) {
		String str = String.format("%s/Android/data/%s/cache/", new Object[] { Environment.getExternalStorageDirectory().getAbsolutePath(), context.getPackageName() });
		File cacheDir = new File(str);
		cacheDir.mkdirs();
		return cacheDir;
	}
}