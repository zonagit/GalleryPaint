package zef.andrade.cs4530.gallerypaint;

import android.graphics.PointF;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zandrade on 9/27/2016.
 *
 * Drawings are saved after every stroke
 */
public class Drawing {
    List<Stroke> mStrokes;
    int mDrawingId;

    public Drawing() {
        mStrokes = new ArrayList<Stroke>();
    }

    public List<Stroke> getStrokes() {

        return mStrokes;
    }

    public void setDrawingId(int drawingId) {
        mDrawingId = drawingId;
    }

    public int getDrawingId() {

        return mDrawingId;
    }

    public int getStrokeCount() {

        return mStrokes.size();
    }

    public Stroke getStroke(int strokeIndex) {
        return mStrokes.get(strokeIndex);
    }

    public void addStroke(Stroke stroke) {

        mStrokes.add(stroke);
    }

    public void loadDrawing(File dir)
    {
        Gson gson = new Gson();
        // read  drawing from the correct file
        String filename = "drawing" + mDrawingId + ".txt";

        try {
            File drawingFile = new File(dir, filename);
            FileReader fileReader = new FileReader(drawingFile);
            BufferedReader reader = new BufferedReader(fileReader);
            String lineJson;

            while ((lineJson = reader.readLine())!=null) {
                Type collectionType = new TypeToken<ArrayList<PointF>>() {}.getType();
                List<PointF> line = gson.fromJson(lineJson, collectionType);
                int color = new Integer(reader.readLine());
                Stroke stroke = new Stroke(color, line);
                addStroke(stroke);
            }
            reader.close();
        }
        catch (Exception e) {
            Log.e("Reading drawings", "Error reading drawing file: " + filename + " Error: " + e.getMessage());
        }
    }

    public void saveDrawing(File dir)
    {
        // dont save empty drawings
        if (mStrokes.size() == 0) {
            return;
        }
        Gson gson = new Gson();
        // write  drawing to the correct file
        String filename = "drawing" + mDrawingId + ".txt";
        try {
            File drawingFile = new File(dir, filename);
            FileWriter fileWriter = new FileWriter(drawingFile, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            for (Stroke stroke : mStrokes) {
                List<PointF> line = stroke.getStrokePoints();
                int color = stroke.getStrokeColor();
                String jsonLine = gson.toJson(line);
                writer.write(jsonLine);
                writer.newLine();
                writer.write(color + "");
                writer.newLine();
            }

            writer.close();

            // update the file holding the total number of drawings if need be
            int numDrawings = Gallery.getInstance().getDrawingCount();
            if (mDrawingId == (numDrawings-1)) {
                drawingFile = new File(dir, "drawingcount.txt");
                fileWriter = new FileWriter(drawingFile, false);
                writer = new BufferedWriter(fileWriter);
                writer.write(numDrawings);
                writer.close();
            }
        }
        catch (Exception e) {
            Log.e("Saving drawings","Error saving drawing file: " + filename + " Error: " + e.getMessage());
        }
    }
}
