package zef.andrade.cs4530.gallerypaint;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.SizeF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zandrade on 9/6/2016.
 *
 * Based on Matt's CircleLayout class
 */
public class PaletteView extends ViewGroup implements PaintSplotchView.OnTouchListener {

    private OnColorChangedListener mOnColorChangedListener;


    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    public void setOnColorChangedListener (OnColorChangedListener onColorChangedListener) {
        mOnColorChangedListener = onColorChangedListener;
    }

    public PaletteView(Context context) {
        super(context);
    }

    public void addColor(int color)
    {
        PaintSplotchView paintView = new PaintSplotchView(this.getContext());
        paintView.setSplotchColor(color);
        paintView.setSplotchId(getChildCount());
        this.addView(paintView);
        invalidate();
    }

    public void removeColor()
    {
        // remove the last color added to palette until one remains
        int numSplotches = getChildCount();
        for (int childIndex = 0; childIndex < numSplotches; childIndex++) {
            PaintSplotchView childView = (PaintSplotchView) getChildAt(childIndex);
            if (childView.getSplotchId() == (numSplotches-1) && numSplotches > 1) {
                // if the color to be removed is highlighted, highlight the color to be deleted
                // next
                if (childView.isHighlighted() && childIndex>0) {
                    PaintSplotchView previousChild = (PaintSplotchView) getChildAt(childIndex-1);
                    previousChild.setHighlighted(true);
                    // trigger a (fake) touch event to update the active color in the PaintAreaView
                    MotionEvent event = MotionEvent.obtain(0l,0l,MotionEvent.ACTION_DOWN,0.0f,0.0f,0);
                    previousChild.onTouchEvent(event);
                }
                this.removeView(childView);
                break;
            }
        }


        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

        int width = widthSpec;
        int height = (int) (heightSpec/1.5);

        setMeasuredDimension(
                resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int i, int i1, int i2, int i3) {

        float theta;
        // TODO: Call measureChild to get the size the child would like to be

        float density = getResources().getDisplayMetrics().density;
        float childWidth = 0.3f * 160.0f * density; // 300/1000 of an inch
        float childHeight = 0.25f * 160.0f * density; // 250/1000 of an inch
        Rect childRect = new Rect();

        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            theta = (float) (2.0 * Math.PI) / (float) getChildCount() * (float) childIndex;

            PointF childCenter = new PointF();
            childCenter.x = getWidth() * 0.5f + (getWidth() * 0.5f - childWidth * 0.5f) * (float) Math.cos(theta);
            childCenter.y = getHeight() * 0.5f + (getHeight() * 0.5f -childHeight * 0.5f) * (float) Math.sin(theta);

            childRect.left = (int) (childCenter.x - childWidth * 0.5f);
            childRect.right = (int) (childCenter.x + childWidth * 0.5f);
            childRect.top = (int) (childCenter.y - childHeight * 0.5f);
            childRect.bottom = (int) (childCenter.y + childHeight * 0.5f);

            View childView = getChildAt(childIndex);
            childView.setOnTouchListener(this);

            childView.layout(childRect.left, childRect.top, childRect.right, childRect.bottom);
        }

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        PaintSplotchView selectedSplotch = (PaintSplotchView) view;
        int selectedId = selectedSplotch.getSplotchId();
        int selectedColor = selectedSplotch.getSplotchColor();
        // Go through all splotches and unselect all but the one that was just selected
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            PaintSplotchView childView = (PaintSplotchView) getChildAt(childIndex);
            int splotchId = childView.getSplotchId();
            if (splotchId == selectedId) {
                childView.setHighlighted(true);
            }
            else {
                childView.setHighlighted(false);
            }
        }
        // Change the color selected in the PaintAreaView
        mOnColorChangedListener.onColorChanged(selectedColor);

        // redraw the PaletteView to update the highlighted color
        invalidate();
        return true;
    }
}
