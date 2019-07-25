package bd.football.coachbook.helper;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class AnimationHelper {
	
	private static final int ANIM_DURATION = 500;
	
	abstract public class EndAnimationListener implements AnimationListener {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	}

	static public void switchAlphaAnimation(View v, float fromAlpha, float toAlpha, int duration,
			AnimationListener listener) {
		Animation alphaAnim = new AlphaAnimation(fromAlpha, toAlpha);
		alphaAnim.setDuration(duration);

		AnimationSet animSet = new AnimationSet(true);
		animSet.addAnimation(alphaAnim);
		animSet.setDuration(ANIM_DURATION);
		if (listener != null) {
			animSet.setAnimationListener(listener);
		}
		v.startAnimation(animSet);
	}
	public static void startTranslateAnimation(final View v, final boolean startFlag,
			final EndAnimationListener listener) {
		startTranslateAnimation(v, startFlag, listener, true);
	}

	public static void startTranslateAnimation(final View v, final boolean visible,
			final EndAnimationListener listener, boolean animationFlag) {

		if (!animationFlag) {
			v.setVisibility(visible ? View.VISIBLE : View.GONE);
			return;
		}

		TranslateAnimation transAnim = new TranslateAnimation(
			Animation.RELATIVE_TO_SELF, 0f,
			Animation.RELATIVE_TO_SELF, 0f,
			Animation.RELATIVE_TO_SELF, visible ? 1f : 0f,
			Animation.RELATIVE_TO_SELF, visible ? 0f : 1f);
		v.setVisibility(View.VISIBLE);
		AnimationSet animSet = new AnimationSet(true);
		animSet.addAnimation(transAnim);
		animSet.setDuration(ANIM_DURATION);
		animSet.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				//AnimatingAwareHelper.setAnimating(true);
			}
	
			@Override
			public void onAnimationEnd(Animation animation) {
				//AnimatingAwareHelper.setAnimating(false);
				v.setVisibility(visible ? View.VISIBLE : View.GONE);
				if (listener != null) {
					listener.onAnimationEnd(animation);
				}
			}
	
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		v.startAnimation(animSet);
	}
	
}
