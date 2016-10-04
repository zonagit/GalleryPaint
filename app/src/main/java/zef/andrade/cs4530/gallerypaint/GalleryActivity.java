package zef.andrade.cs4530.gallerypaint;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by zandrade on 9/26/2016.
 *
 * The current drawing is saved every time we flip back or forward or every time we pause the app
 * All files are loaded on start
 *
 */
public class GalleryActivity extends AppCompatActivity implements PaintAreaView.OnNewDrawingListener,
        SideMenu.OnForwardListener, SideMenu.OnBackListener, SideMenu.OnPlayListener, SideMenu.OnStopListener,
        ColorControl.OnClickListener, ValueAnimator.AnimatorUpdateListener {

    private PaintAreaView mPaintAreaView;
    private Drawing mDrawing;
    private SideMenu mSideMenu;
    private final int PICK_SELECTED_COLOR = 1;
    public static final String COLOR_SELECTED = "Color_Selected";
    private static int selectedColor = Color.YELLOW;
    ColorControl colorControl;
    private final String mPaletteColorFile = "currentPaletteColor.txt";
    private ValueAnimator mAnimator;

    @Override
    public void onClick(View view) {
       // Open the PalleteActivity
         Intent openPalleteActivityIntent = new Intent();
         openPalleteActivityIntent.setClass(this, PaletteActivity.class);
         startActivityForResult(openPalleteActivityIntent, PICK_SELECTED_COLOR);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_SELECTED_COLOR) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                int newColor = data.getIntExtra(COLOR_SELECTED, selectedColor);
                colorControl.setSplotchColor(newColor);
                mPaintAreaView.setActiveColor(newColor);
                //save the current color
                saveCurrentColor();
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        mAnimator = new ValueAnimator();
        mAnimator.setDuration(5000);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(this);

        mPaintAreaView = new PaintAreaView(this);
        mPaintAreaView.setOnNewDrawingListener(this);
        rootLayout.addView(mPaintAreaView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));

        mSideMenu = new SideMenu(this);
        mSideMenu.setBackgroundColor(Color.GRAY);
        mSideMenu.setOnBackListener(this);
        mSideMenu.setOnForwardListener(this);
        mSideMenu.setOnPlayListener(this);
        mSideMenu.setOnStopListener(this);
        colorControl = mSideMenu.getColorControl();
        colorControl.setOnClickListener(this);
        colorControl.setSplotchColor(selectedColor);
        mSideMenu.getResetButton().setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        deleteAllFiles();
                    }}
                    );
        rootLayout.addView(mSideMenu, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(rootLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mDrawing = mPaintAreaView.getDrawing();
        mDrawing.saveDrawing(getFilesDir());

    }

    @Override
    protected void onResume() {
        super.onResume();

        //deleteAllFiles();
        if (Gallery.getInstance().getDrawingCount() == 0) {
            // Load all drawings and add them up to the gallery
            loadDrawings();
            Gallery.getInstance().setCurrentDrawingIndex(0);

            // load first drawing into the view
            if (Gallery.getInstance().getDrawingCount() > 0) {
                loadDrawingIntoView();
            }
        }
        loadCurrentColor();
        mSideMenu.handleEnableDisableOfButtons();
        mSideMenu.handlePlayStopButtons();
    }

    private void loadDrawingIntoView() {
        int currentDrawingIndex = Gallery.getInstance().getCurrentDrawingIndex();
        mDrawing = Gallery.getInstance().getDrawing(currentDrawingIndex);

        mPaintAreaView.setDrawing(mDrawing);
    }

    private void saveCurrentColor() {
        try {
            File colorFile = new File(getFilesDir(), mPaletteColorFile);
            FileWriter fileWriter = new FileWriter(colorFile, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            int activeColor = mPaintAreaView.getActiveColor();
            writer.write(activeColor + "\n");
            writer.close();
        }
        catch (Exception e) {
            Log.e("Saving current color","Error saving color file: " + mPaletteColorFile + " Error: " + e.getMessage());
        }
    }

    private void loadCurrentColor() {
        try {
            File colorFile = new File(getFilesDir(), mPaletteColorFile);
            FileReader fileReader = new FileReader(colorFile);
            BufferedReader reader = new BufferedReader(fileReader);
            int currentColor = Integer.parseInt(reader.readLine());
            mPaintAreaView.setActiveColor(currentColor);
            mSideMenu.getColorControl().setSplotchColor(currentColor);
            reader.close();
        }
        catch (Exception e) {
            Log.e("Loading current color","Error loading color file: " + mPaletteColorFile + " Error: " + e.getMessage());
        }
    }

    private void saveDrawings() {
        Gson gson = new Gson();
        // write each drawing to a different file
        List<Drawing> drawings = Gallery.getInstance().getDrawings();
        for (int drawingIndex =0; drawingIndex < drawings.size(); drawingIndex++) {
            Drawing drawing = drawings.get(drawingIndex);
            List<Stroke> strokes = drawing.getStrokes();
            String filename = "drawing" + drawingIndex + ".txt";
            try {
                File drawingFile = new File(getFilesDir(),filename);
                FileWriter fileWriter = new FileWriter(drawingFile, false);
                BufferedWriter writer = new BufferedWriter(fileWriter);
                for (Stroke stroke : strokes) {
                    List<PointF> line = stroke.getStrokePoints();
                    int color = stroke.getStrokeColor();
                    String jsonLine = gson.toJson(line);
                    writer.write(jsonLine);
                    writer.newLine();
                    writer.write(color);
                    writer.newLine();
                }

                writer.close();
            }
            catch (Exception e) {
                Log.e("Saving drawings","Error saving drawing file: " + filename + " Error: " + e.getMessage());
            }
        }

        try {
            File countingFile = new File(getFilesDir(), "drawingcount.txt");
            FileWriter fileWriter = new FileWriter(countingFile, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(drawings.size());
        }
        catch (Exception e) {
            Log.e("Saving drawings","Error saving drawing file: " + "drawingcount.txt" + " Error: " + e.getMessage());
        }
    }

    private void loadDrawings() {
        Gson gson = new Gson();
        int numDrawings = 0;
        // read all pre-existing drawings from the file system and add each to the gallery
        try {
            File countingFile = new File(getFilesDir(), "drawingcount.txt");
            FileReader fileReader = new FileReader(countingFile);
            BufferedReader reader = new BufferedReader(fileReader);
            numDrawings = reader.read();
        } catch (Exception e) {
            Log.e("Reading drawings", "Error reading drawing file: " + "drawingcount.txt" + " Error: " + e.getMessage());
        }

        //read drawing and add each to the gallery
        for (int drawingIndex = 0; drawingIndex < numDrawings; drawingIndex++) {
            String filename = "drawing" + drawingIndex + ".txt";
            Drawing drawing = new Drawing();
            drawing.setDrawingId(drawingIndex);

            try {
                File drawingFile = new File(getFilesDir(), filename);
                FileReader fileReader = new FileReader(drawingFile);
                BufferedReader reader = new BufferedReader(fileReader);
                String lineJson;

                while ((lineJson = reader.readLine())!=null) {
                    Type collectionType = new TypeToken<ArrayList<PointF>>() {}.getType();
                    List<PointF> line = gson.fromJson(lineJson, collectionType);

                    int color = new Integer(reader.readLine());
                    Stroke stroke = new Stroke(color, line);
                    drawing.addStroke(stroke);
                }
                Gallery.getInstance().addDrawing(drawing);
                reader.close();
            }
            catch (Exception e) {
                Log.e("Reading drawings", "Error reading drawing file: " + filename + " Error: " + e.getMessage());
            }

        }

    }

    public void deleteAllFiles() {
        File dir = new File(getFilesDir() + "");
        String[] files = dir.list();
        for (int i=0;i< files.length; i++) {
           // if (files[i].contains("drawing")) {
                new File(dir, files[i]).delete();
            //}
        }
    }

    @Override
    public void moveBackListener() {
        int currentIndex = Gallery.getInstance().getCurrentDrawingIndex();
        int numDrawings = Gallery.getInstance().getDrawingCount();

        if (numDrawings > 0) {
            // save current drawing
            mDrawing.saveDrawing(getFilesDir());
            // decrease currentIndex
            currentIndex -= 1;
            Gallery.getInstance().setCurrentDrawingIndex(currentIndex);
            // load previous drawing; assume it is in the gallery
            mDrawing = Gallery.getInstance().getDrawing(currentIndex);
            mPaintAreaView.setDrawing(mDrawing);
            mPaintAreaView.resetBitMap();
            mPaintAreaView.drawStrokes();
            mPaintAreaView.invalidate();
        }
        mSideMenu.handlePlayStopButtons();
    }

    @Override
    public void moveForwardListener() {
        int currentIndex = Gallery.getInstance().getCurrentDrawingIndex();
        int numDrawings = Gallery.getInstance().getDrawingCount();
        if (numDrawings>0) {
            // save current drawing
            mDrawing.saveDrawing(getFilesDir());
            currentIndex += 1;
            // increase currentIndex
            Gallery.getInstance().setCurrentDrawingIndex(currentIndex);
            // create new drawing and update the paint area view
            if (currentIndex == numDrawings) {
                mDrawing = new Drawing();
                mDrawing.setDrawingId(currentIndex);
            }
            // load next drawing
            else if (currentIndex < numDrawings) {
                mDrawing = Gallery.getInstance().getDrawing(currentIndex);
            }
            mPaintAreaView.setDrawing(mDrawing);
            mPaintAreaView.resetBitMap();
            mPaintAreaView.drawStrokes();

            mPaintAreaView.invalidate();
        }
        mSideMenu.handlePlayStopButtons();
    }

    @Override
    public void updateGalleryListener() {
        if (Gallery.getInstance().getDrawingCount() == Gallery.getInstance().getCurrentDrawingIndex()) {
            mDrawing = mPaintAreaView.getDrawing();
            mDrawing.setDrawingId(Gallery.getInstance().getCurrentDrawingIndex());
            Gallery.getInstance().addDrawing(mDrawing);
        }
        mDrawing.saveDrawing(getFilesDir());
        // handle enable/disable of back and forward buttons
        mSideMenu.handleEnableDisableOfButtons();
        mSideMenu.handlePlayStopButtons();
    }

    @Override
    public void playListener() {
        // Disable touch events in the paint area view
        mPaintAreaView.setInPlay(true);
        // animate current drawing
        // get number of strokes of drawing
        if (mDrawing != null) {
            mPaintAreaView.setCurrentStrokeIndex(-1);
            // clear all strokes
            mPaintAreaView.resetBitMap();
            int numStrokes = mDrawing.getStrokeCount();
            mAnimator.setIntValues(0,numStrokes);
//            IntEvaluator evaluator = new IntEvaluator();
//            mAnimator.setEvaluator(evaluator);
            mAnimator.start();
        }
    }

    @Override
    public void stopListener() {
        mAnimator.cancel();
        //Draw remaining strokes
        int currentStroke = mPaintAreaView.getCurrentStrokeIndex();
        for (int i= currentStroke; i< mDrawing.getStrokeCount(); i++) {
            mPaintAreaView.drawOneStroke(i);
        }
        mPaintAreaView.invalidate();
        // restore ui
        mSideMenu.handleEnableDisableOfButtons();
        mSideMenu.handlePlayStopButtons();
        colorControl.setEnabled(true);
        mSideMenu.getResetButton().setEnabled(true);
        mPaintAreaView.setInPlay(false);
        mPaintAreaView.setCurrentStrokeIndex(-1);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int strokeIndex = (Integer) valueAnimator.getAnimatedValue();
        if (mDrawing.getStrokeCount() <= strokeIndex) {
            // restore ui
            stopListener();
            return;
        }
        if (mPaintAreaView.getCurrentStrokeIndex() < 0 && strokeIndex == 0) {
            mPaintAreaView.setCurrentStrokeIndex(0);
            mPaintAreaView.drawOneStroke(0);

        }
        else if (mPaintAreaView.getCurrentStrokeIndex() < strokeIndex) {
            mPaintAreaView.setCurrentStrokeIndex(strokeIndex);
            mPaintAreaView.drawOneStroke(strokeIndex);
            mPaintAreaView.invalidate();
        }
    }
}
