package com.eke.cust.global;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class ColorBorderDrawable extends Drawable {
	private Paint mPaint;
	private RectF rectF;
	
	public ColorBorderDrawable(boolean isSelected) {
		Random random = new Random();
		int r = random.nextInt(256);
        int g= random.nextInt(256);
        int b = random.nextInt(256);
        
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(6.f);
		
		if (isSelected) {
			mPaint.setColor(0xff9f8dff);
		}
		else {
			mPaint.setColor(Color.rgb(r, g, b));		
			mPaint.setStyle(Paint.Style.STROKE); 
		}
		
	}
	
	@Override  
    public void setBounds(int left, int top, int right, int bottom) {  
        super.setBounds(left, top, right, bottom);  
        rectF = new RectF(left, top, right, bottom);  
    }
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawRoundRect(rectF, 10, 10, mPaint);
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

}
