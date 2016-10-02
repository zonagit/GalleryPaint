package zef.andrade.cs4530.gallerypaint;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
        SideMenu.OnForwardListener, SideMenu.OnBackListener{

    private PaintAreaView mPaintAreaView;
    private Drawing mDrawing;
    private SideMenu mSideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        mPaintAreaView = new PaintAreaView(this);
        mPaintAreaView.setOnNewDrawingListener(this);
        rootLayout.addView(mPaintAreaView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));

        mSideMenu = new SideMenu(this);
        mSideMenu.setBackgroundColor(Color.GRAY);
        mSideMenu.setOnBackListener(this);
        mSideMenu.setOnForwardListener(this);
        rootLayout.addView(mSideMenu, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));


        setContentView(rootLayout);


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
        // Load all drawings and add them up to the gallery
        loadDrawings();

        Gallery.getInstance().setCurrentDrawingIndex(0);

        // load first drawing in the view
        if (Gallery.getInstance().getDrawingCount()> 0) {
            loadDrawingIntoView();
        }

        mSideMenu.handleEnableDisableOfButtons();
    }

    private void loadDrawingIntoView() {
        int currentDrawingIndex = Gallery.getInstance().getCurrentDrawingIndex();
        mDrawing = Gallery.getInstance().getDrawing(currentDrawingIndex);

        mPaintAreaView.setDrawing(mDrawing);
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
            if (files[i].contains("drawing")) {
                new File(dir, files[i]).delete();
            }
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
    }
}
