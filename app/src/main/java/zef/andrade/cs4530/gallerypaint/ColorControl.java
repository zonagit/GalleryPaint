package zef.andrade.cs4530.gallerypaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zandrade on 10/1/2016.
 *
 * I could derive this class from PaintSplotchView but PaintSplotch view detects touchEvent rather than the clickevent
 * The best way would be to derive both ColorControl and PaintSplotchView from a common base class but
 * seems pointless to do that just to draw an oval.
 */
public class ColorControl extends View {

    private int mSplotchColor = Color.WHITE;

    public ColorControl(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF splotchRect = new RectF();
        splotchRect.left = getPaddingLeft();
        splotchRect.top = getPaddingTop();
        splotchRect.right = getWidth() - getPaddingRight();
        splotchRect.bottom = getHeight() - getPaddingBottom();

        Paint splotchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        splotchPaint.setColor(mSplotchColor);

        canvas.drawOval(splotchRect, splotchPaint);
    }

    public int getSplotchColor() {
        return mSplotchColor;
    }

    public void setSplotchColor (int splotchColor) {
        this.mSplotchColor = splotchColor;
        invalidate();
    }
}