/*
 *
 *  *
 *  *  *
 *  *  *  * ===================================
 *  *  *  * Copyright (c) 2016.
 *  *  *  * 浣滆�咃細瀹夊崜鐚�
 *  *  *  * 寰崥锛欯瀹夊崜鐚�
 *  *  *  * 鍗氬锛歨ttp://sunjiajia.com
 *  *  *  * Github锛歨ttps://github.com/opengit
 *  *  *  *
 *  *  *  * 娉ㄦ剰**锛氬鏋滄偍浣跨敤鎴栬�呬慨鏀硅浠ｇ爜锛岃鍔″繀淇濈暀姝ょ増鏉冧俊鎭��
 *  *  *  * ===================================
 *  *  *
 *  *  *
 *  *
 *
 */

package com.softpo.teaencyclopedia.help;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

  public RoundedImageView(Context context) {
    super(context);
  }

  public RoundedImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onDraw(Canvas canvas) {

    Drawable drawable = getDrawable();

    if (drawable == null) {
      return;
    }

    if (getWidth() == 0 || getHeight() == 0) {
      return;
    }

    Bitmap b = ((BitmapDrawable) drawable).getBitmap();
    Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

    int w = getWidth();

    Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
    canvas.drawBitmap(roundBitmap, 0, 0, null);
  }

  public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {

    Bitmap sbmp;

    if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
      sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
    } else {
      sbmp = bmp;
    }

    Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

    paint.setAntiAlias(true);
    paint.setFilterBitmap(true);
    paint.setDither(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(Color.parseColor("#BAB399"));
    canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
        sbmp.getWidth() / 2 + 0.1f, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(sbmp, rect, rect, paint);

    return output;
  }
}