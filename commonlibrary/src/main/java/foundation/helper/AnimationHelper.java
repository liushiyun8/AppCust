package foundation.helper;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

public class AnimationHelper {

	public static Animation createFastRotateAnimation() {
		Animation rotate = new RotateAnimation(-1.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);

		rotate.setRepeatMode(Animation.REVERSE);
		rotate.setRepeatCount(Animation.INFINITE);
		rotate.setDuration(80);
		rotate.setInterpolator(new AccelerateDecelerateInterpolator());
		return rotate;
	}
	
	 public static void addAnimation(View view) {
		float[] vaules = new float[] { 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f,
				1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f };
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
		set.setDuration(150);
		set.start();
	}



	/**
	 * 开始震动
	 *
	 * @param editTexts
	 */
	public  static void shake(EditText... editTexts) {
		for (EditText editText : editTexts) {
			editText.startAnimation(shakeAnimation(5));
		}


	}

	// CycleTimes动画重复的次数
	private static Animation shakeAnimation(int CycleTimes) {
		Animation translateAnimation = new TranslateAnimation(0, 10, 0, 10);
		translateAnimation.setInterpolator(new CycleInterpolator(CycleTimes));
		translateAnimation.setDuration(1000);
		return translateAnimation;
	}
}
