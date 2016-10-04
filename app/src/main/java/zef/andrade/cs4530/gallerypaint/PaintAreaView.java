package zef.andrade.cs4530.gallerypaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zandrade on 9/6/2016.
 *
 * This is the view where the user draws/paints
 *
 * For a nice starting point look at the tutorial
 *
 * http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-touch-interaction--mobile-19202
 *
 */
public class PaintAreaView extends View {
    public interface OnNewDrawingListener {
        void updateGalleryListener();
    }

    public static final int BACKGROUND_PALETTE_COLOR = Color.BLACK;
    public static final int DEFAULT_ACTIVE_COLOR = Color.YELLOW;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;

    private int mActiveColor;// current path color

    // a list of all paths in the paint area view (a drawing)
    private Drawing mDrawing;

    // a new path being traced by the user
    private Stroke mNewPath;

    private OnNewDrawingListener updateGalleryListener;

    public void setOnNewDrawingListener(OnNewDrawingListener onNewDrawingListener) {
        updateGalleryListener = onNewDrawingListener;
    }

    public int getActiveColor() {
        return mActiveColor;
    }

    public void setActiveColor(int color) {
        mActiveColor = color;
    }

    public void resetBitMap() {
       // mBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas.drawColor(BACKGROUND_PALETTE_COLOR);

    }

    public Drawing getDrawing() {
        return mDrawing;
    }
    // since all points are kept in unit space coordinates we
    // use this matrix to convert from unit coordinates to the physical x,y
    // position coordinates of the device
    private Matrix mUnitMatrix;


    public PaintAreaView(Context context) {
        super(context);

        //TODO: Load the first drawing in the collection if any
        mDrawing = new Drawing();
        mCanvas = null;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GREEN);
        mNewPath = null;
        mActiveColor = DEFAULT_ACTIVE_COLOR;// default path color
        mUnitMatrix = new Matrix();
    }

    public void setDrawing(Drawing drawing) {

        mDrawing = drawing;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // convert touch point to unit coordinate space
        PointF newPoint = new PointF();
        newPoint.set(event.getX()/getWidth(),event.getY()/getHeight());

        int eventAction = event.getAction();

        switch (eventAction) {
            // start of new path at newPoint
            case MotionEvent.ACTION_DOWN:
                mNewPath = new Stroke(mActiveColor, newPoint);
                invalidate();// invalidate view to force onDraw to execute
                break;
            // extend new path by adding current point to it
            case MotionEvent.ACTION_MOVE:
                mNewPath.addPointToPath(newPoint);
                invalidate();// invalidate view to force onDraw to execute
                break;
            // done building path, draw it in the bitmap then add it to list of palette paths
            case MotionEvent.ACTION_UP:
                mNewPath.drawPalettePath(mCanvas, mPaint, mUnitMatrix);
                mDrawing.addStroke(mNewPath);
                updateGalleryListener.updateGalleryListener();
                // clear path
                mNewPath = null;
                break;
            default:
                return false;
        }

        return true;
    }

    public void drawStrokes() {
        List<Stroke> strokes = mDrawing.getStrokes();
        for (Stroke stroke : strokes) {
            stroke.drawPalettePath(mCanvas, mPaint,mUnitMatrix);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // dump the bitmap with all existing paths to the canvas
        canvas.drawBitmap(mBitmap, 0, 0, null);

        // draw the new palette path as well (not yet in the bitmap)
        if (mNewPath != null) {
            mNewPath.drawPalettePath(canvas, mPaint, mUnitMatrix);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mUnitMatrix.reset();
        mUnitMatrix.setScale(w,h);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(BACKGROUND_PALETTE_COLOR);

        // draw existing paths
        for (Stroke path : mDrawing.getStrokes()) {
            path.drawPalettePath(mCanvas, mPaint, mUnitMatrix);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

        int width = widthSpec;
        int height = (int)(heightSpec/1.2);

        setMeasuredDimension(
                resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));

    }
}
