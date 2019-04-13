package com.devapp.devmain.util;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by u_pendra on 28/3/17.
 */

public class TextDrawable extends Drawable {

    public static float SIZE_16 = 16f;
    public static float SIZE_18 = 18f;
    public static float SIZE_20 = 20f;
    private final String text;
    private final Paint paint;

    public TextDrawable(String text, int color, float size) {
        this.text = text;
        this.paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text, 0, 6, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}