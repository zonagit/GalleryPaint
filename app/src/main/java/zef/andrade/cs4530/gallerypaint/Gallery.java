package zef.andrade.cs4530.gallerypaint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zandrade on 9/27/2016.
 */
public class Gallery {
    static Gallery sInstance = null;
    static List<Drawing> sGalleryDrawings = null;
    static int sCurrentDrawingIndex;

    public static Gallery getInstance() {
        if (sInstance == null) {
            sInstance = new Gallery();
            sGalleryDrawings = new ArrayList<Drawing>();
            sCurrentDrawingIndex = 0;
        }

        return sInstance;
    }

    public List<Drawing> getDrawings() {
        return sGalleryDrawings;
    }

    public void setCurrentDrawingIndex (int index) {
        sCurrentDrawingIndex = index;
    }

    public int getCurrentDrawingIndex() {
        return sCurrentDrawingIndex;
    }

    int getDrawingCount() {

        return sGalleryDrawings.size();
    }

    Drawing getDrawing(int drawingIndex) {

        if (drawingIndex< sGalleryDrawings.size()) {
            return sGalleryDrawings.get(drawingIndex);
        }

        return null;
    }

    void addDrawing(Drawing drawing) {
       sGalleryDrawings.add(drawing);
    }

    void updateDrawing(int drawingIndex) {

    }
}
