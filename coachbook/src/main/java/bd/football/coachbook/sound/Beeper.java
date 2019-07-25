package bd.football.coachbook.sound;

import android.content.Context;
import android.media.MediaPlayer;

public class Beeper {

	MediaPlayer player;

	private static Beeper mInstance;
	private static Context mContext;

	public static Beeper getInstance() {
		if (mInstance == null) {
			synchronized (Beeper.class) {
				if (mInstance == null) {
					mInstance = new Beeper();
				}
			}
		}
		return mInstance;
	}

	public static void init(Context context) {
		mContext = context;
	}

	private Beeper() {
	}

	public void play(int resId) {
		try {
			if(player != null && player.isPlaying()){
				return;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			player = MediaPlayer.create(mContext, resId);
			player.seekTo(0);
			player.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		if (player == null) {
			return;
		}
		try {
			if(player.isPlaying()){
				player.stop();
				player.release();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
