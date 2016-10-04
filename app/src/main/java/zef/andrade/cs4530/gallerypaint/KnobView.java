package zef.andrade.cs4530.gallerypaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zandrade on 9/6/2016.
 */
public class KnobView extends View {
    public interface OnAngleChangedListener {
        void onAngleChanged(float theta);
    }


    private float mTheta = 0.0f;
    private RectF mKnobRect = new RectF();
    private OnAngleChangedListener mAngleChangedListener = null;
    private int[] mKnobColor;


    public KnobView(Context context) {
        super(context);

        mKnobColor = new int[]{255,0,0};
    }

    public void setOnAngleChangedListener(OnAngleChangedListener listener) {
        mAngleChangedListener = listener;
    }

    public void setTheta(float theta) {
        mTheta = theta;
        invalidate();
    }

    public void setKnobColor(int[] color) {
        mKnobColor = color;
        invalidate();
    }

    public int getKnobColor() {
        for (int i=0;i<3;i++) {
            if (mKnobColor[i] != 0) {
                return mKnobColor[i];
            }
        }
        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF touchPoint = new PointF();
        touchPoint.x = event.getX();
        touchPoint.y = event.getY();

        float theta = (float) Math.atan2(
                touchPoint.y - mKnobRect.centerY(),
                touchPoint.x - mKnobRect.centerX());
        setTheta(theta);
        for (int i=0;i<3;i++) {
            if (mKnobColor[i] != 0) {
                mKnobColor[i] = 255 + (int) (theta/(Math.PI) * 255);
            }
        }
        mAngleChangedListener.onAngleChanged(theta);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mKnobRect.left = getPaddingLeft();
        mKnobRect.top = getPaddingTop();
        mKnobRect.right = getWidth()-getPaddingRight();
        mKnobRect.bottom = mKnobRect.width();
        float offset = (getHeight()- mKnobRect.height())*0.5f;
        mKnobRect.top += offset;
        mKnobRect.bottom += offset;

        float radius = mKnobRect.width() * 0.35f;

        PointF nibCenter = new PointF();
        nibCenter.x = mKnobRect.centerX() + (float) (radius * Math.cos((double) mTheta));
        nibCenter.y= mKnobRect.centerY() + (float) (radius * Math.sin((double) mTheta));

        float nibRadius = radius * 0.2f;

        RectF nibRect = new RectF();
        nibRect.left = nibCenter.x - nibRadius;
        nibRect.top = nibCenter.y - nibRadius;
        nibRect.right = nibCenter.x + nibRadius;;
        nibRect.bottom = nibCenter.y + nibRadius;

        Paint knobPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        knobPaint.setColor(Color.rgb(mKnobColor[0],mKnobColor[1],mKnobColor[2]));

        Paint knibPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        knibPaint.setColor(Color.YELLOW);

        canvas.drawOval(mKnobRect, knobPaint);
        canvas.drawOval(nibRect, knibPaint);

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50.0f);
        int red = Color.red(knobPaint.getColor());
        int green = Color.green(knobPaint.getColor());
        int blue = Color.blue(knobPaint.getColor());
        if (red>0)
            canvas.drawText(red + "", (float)mKnobRect.centerX(), (float) mKnobRect.centerY(),textPaint);
        else if (green>0)
            canvas.drawText(green + "", (float)mKnobRect.centerX(), (float) mKnobRect.centerY(),textPaint);
        else if (blue>0)
            canvas.drawText(blue + "", (float)mKnobRect.centerX(), (float) mKnobRect.centerY(),textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(350,350);
//                resolveSize(widthSpec/4, widthMeasureSpec),
//                resolveSize(350, heightMeasureSpec)

       // );
   }
}
