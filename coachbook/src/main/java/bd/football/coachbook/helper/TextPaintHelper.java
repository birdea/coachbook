package bd.football.coachbook.helper;

import android.graphics.Paint;

public class TextPaintHelper {

	public static float getTextWidths(String text, Paint paint){
		float[] charWidths = new float[text.length()];
		paint.getTextWidths(text, charWidths);
		float sumX = 0;
		for (float widths : charWidths) {
			sumX += widths;
		}
		return sumX;
	}
}
