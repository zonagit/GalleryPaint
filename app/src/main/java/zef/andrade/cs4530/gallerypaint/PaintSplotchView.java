package zef.andrade.cs4530.gallerypaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zandrade on 9/6/2016.
 *
 * Based on Matt's SplotchView class
 */
public class PaintSplotchView extends View {

    static final int SPLOTCH_HIGHLIGHT_COLOR = Color.YELLOW;
    static final int ALTERNATE_HIGHLIGHT_COLOR = Color.RED;

    private int mSplotchId;// unique id
    private int mSplotchColor = Color.WHITE;
    private boolean mHighlighted = false;// same as active
    private OnTouchListener mOnTouchListener;

    public int getSplotchId() {
        return mSplotchId;
    }

    public void setSplotchId(int id) {
        mSplotchId = id;
    }


    public int getSplotchColor() {
        return mSplotchColor;
    }

    public void setSplotchColor (int splotchColor) {
        this.mSplotchColor = splotchColor;
        invalidate();
    }

    public boolean isHighlighted() {

        return mHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.mHighlighted = highlighted;
        invalidate();
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    public PaintSplotchView(Context context) {

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

        if (mHighlighted) {
            Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // TODO: What if the splotch is the same color as the highlight color
            highlightPaint.setColor(SPLOTCH_HIGHLIGHT_COLOR);
            if (mSplotchColor == SPLOTCH_HIGHLIGHT_COLOR) {
                highlightPaint.setColor(ALTERNATE_HIGHLIGHT_COLOR);
            }
            else if (mSplotchColor == ALTERNATE_HIGHLIGHT_COLOR) {
                highlightPaint.setColor(SPLOTCH_HIGHLIGHT_COLOR);
            }
            highlightPaint.setStyle(Paint.Style.STROKE);
            highlightPaint.setStrokeWidth(splotchRect.height() * 0.1f);
            canvas.drawOval(splotchRect, highlightPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOnTouchListener.onTouch(this, event);
                break;
            default:
                return false;
        }
        return true;
    }
}
