package zef.andrade.cs4530.gallerypaint;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by zandrade on 9/6/2016.
 */
public class KnobColorSelectorView extends LinearLayout implements KnobView.OnAngleChangedListener {

    public interface OnPlusListener {
        int getViews();
        void addColorListener(int color);
    }

    public interface OnMinusListener {
        int getViews();
        void removeColorListener();
    }

    private LinearLayout mKnobLayout;
    private LinearLayout mButtonLayout;
    private KnobView mRedKnob;
    private KnobView mGreenKnob;
    private KnobView mBlueKnob;
    private  Button mPlus;
    private  Button mMinus;
    private OnPlusListener mOnPlusListener;
    private OnMinusListener mOnMinusListener;

    public void setOnPlusListener(OnPlusListener onPlusListener) {
        mOnPlusListener = onPlusListener;
    }

    public void setOnMinusListener(OnMinusListener onMinusListener) {
        mOnMinusListener = onMinusListener;
    }

    public KnobColorSelectorView(Context context) {

        super(context);
        this.setOrientation(LinearLayout.HORIZONTAL);

        // 3 knob colors in a row
        mKnobLayout = new LinearLayout(context);
        mKnobLayout.setOrientation(LinearLayout.HORIZONTAL);

        mRedKnob = new KnobView(context);
        mRedKnob.setKnobColor(new int[]{255,0,0});
        mRedKnob.setOnAngleChangedListener(this);

        mKnobLayout.addView(mRedKnob,new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));

        mGreenKnob = new KnobView(context);
        mGreenKnob.setKnobColor(new int[]{0,255,0});
        mGreenKnob.setOnAngleChangedListener(this);
        mKnobLayout.addView(mGreenKnob,new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));

        mBlueKnob = new KnobView(context);
        mBlueKnob.setKnobColor(new int[]{0,0,255});
        mBlueKnob.setOnAngleChangedListener(this);
        mKnobLayout.addView(mBlueKnob,new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));

        this.addView(mKnobLayout,new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));

        // +/- buttons vertically
        mButtonLayout = new LinearLayout(this.getContext());
        mButtonLayout.setOrientation(LinearLayout.VERTICAL);
        mButtonLayout.setBackgroundColor(Color.GRAY);
        mPlus = new Button(this.getContext());
        mPlus.setText("+");
        mPlus.setTextSize(20);
        mPlus.setOnTouchListener(plusListener);
        mButtonLayout.addView(mPlus,new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));
        mMinus = new Button(context);
        mMinus.setText("-");
        mMinus.setTextSize(20);
        mMinus.setOnTouchListener(minusListener);
        mButtonLayout.addView(mMinus,new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));

        this.addView(mButtonLayout,new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));
    }

 //   @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
//
//        float childWidth = getWidth()/4;
//        float childHeight = getHeight()/4;
//        Rect childRect = new Rect();
//
//        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
//            childRect.left = (int) (childWidth * childIndex);
//            childRect.right = (int) (childWidth * (childIndex +1));
//            childRect.top = 100;
//            childRect.bottom = (int) (childHeight);
//
//            View childView = getChildAt(childIndex);
//            childView.layout(childRect.left, childRect.top, childRect.right, childRect.bottom);
//        }
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

        int width = widthSpec;
        int height = heightSpec;

        setMeasuredDimension(
                resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    public void onAngleChanged(float theta) {

    }

    OnTouchListener plusListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Read the current color in the 3 knobs
                    int redColor = mRedKnob.getKnobColor();
                    int blueColor = mBlueKnob.getKnobColor();
                    int greenColor = mGreenKnob.getKnobColor();

                    mOnPlusListener.addColorListener(Color.rgb(redColor, greenColor, blueColor));
                    if (mOnPlusListener.getViews()>1) {
                        mMinus.setEnabled(true);
                    }
                    break;
                default:
                    return false;
            }

            return true;
        }
    };

    OnTouchListener minusListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // remove the last splotch added and
                    // disable the - button if there is only one splotch
                    mOnMinusListener.removeColorListener();
                    if (mOnMinusListener.getViews() == 1) {
                        view.setEnabled(false);
                    }

                    break;
                default:
                    return false;
            }

            return true;
        }
    };
}
