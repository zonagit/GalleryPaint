package zef.andrade.cs4530.gallerypaint;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zandrade on 9/6/2016.
 *
 * Represents a path in the paint area view: the points on the path, the rgb color of the path
 */
public class Stroke {
    private Path mPath;
    private List<PointF> mPathPoints;
    private int mPathColor;

    public Stroke(int color, PointF startPoint) {
        mPath = new Path();
        mPath.moveTo(startPoint.x, startPoint.y);
        mPathPoints = new ArrayList<PointF>();
        mPathPoints.add(startPoint);
        mPathColor = color;
    }

    public Stroke(int color, List<PointF> points) {
        mPath = new Path();
        mPath.moveTo(points.get(0).x, points.get(0).y);
        mPathPoints = new ArrayList<PointF>();
        mPathPoints.add(points.get(0));
        mPathColor = color;
        for (int i=1; i<points.size();i++) {
            addPointToPath(points.get(i));
        }
    }

    public List<PointF> getStrokePoints() {
        return mPathPoints;
    }

    public void setStrokePoints(List<PointF> mPathPoints) {

    }

    public int getStrokeColor() {
        return mPathColor;
    }

    public void addPointToPath(PointF point) {
        // add a quadratic Bezier segment from the previous point to the
        // point halfway between the previous point and the new point
        PointF prevPoint = mPathPoints.get(mPathPoints.size()-1);
        mPath.quadTo(prevPoint.x, prevPoint.y, (point.x + prevPoint.x)/2, (point.y + prevPoint.y)/2);

        // add new point to current list of path points
        mPathPoints.add(point);
    }

    public void drawPalettePath(Canvas canvas, Paint paint, Matrix unitMatrix) {
        paint.setColor(mPathColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        Path path = new Path();
        path.set(mPath);
        path.transform(unitMatrix);
        canvas.drawPath(path, paint);
    }

}
