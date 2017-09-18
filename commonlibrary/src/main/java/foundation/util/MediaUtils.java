package foundation.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class MediaUtils {

	public static int getDuration(Context context, String audioPath) {
		int duration = 0;
		MediaPlayer mp = null;
		try {
			mp = MediaPlayer.create(context, Uri.parse(audioPath));
			if (mp != null) {
				duration = mp.getDuration();// ms
			}
		} catch (Exception e) {
		} finally {
			if (mp != null)
				mp.release();
			mp = null;
		}
		return duration;
	}
}
