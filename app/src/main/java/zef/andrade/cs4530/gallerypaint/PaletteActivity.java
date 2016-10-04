package zef.andrade.cs4530.gallerypaint;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PaletteActivity extends AppCompatActivity implements PaletteView.OnColorChangedListener,
        KnobColorSelectorView.OnPlusListener, KnobColorSelectorView.OnMinusListener {

    private final String mPaletteFile = "paletteColors.txt";
    private PaletteView mPaletteView;
    private KnobColorSelectorView mKnobColorSelectorView;
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mLayout = new LinearLayout(this);
        mLayout.setOrientation(LinearLayout.VERTICAL);

        mPaletteView = new PaletteView(this);
        mPaletteView.setBackgroundColor(Color.rgb(222,184,135));
        mPaletteView.setOnColorChangedListener(this);

        createKnobSelectorView();

        mLayout.addView(mPaletteView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        mLayout.addView(mKnobColorSelectorView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,0));//new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));

        setContentView(mLayout);

    }

    private void addColorsToPaletteView() {

        // prepopulate the palette with 5 different colors: red, green, blue, yellow, and purple.
        PaintSplotchView redPaintSplotchView = new PaintSplotchView(this);
        redPaintSplotchView.setSplotchColor(Color.RED);
        redPaintSplotchView.setSplotchId(0);
        mPaletteView.addView(redPaintSplotchView);

        PaintSplotchView greenPaintSplotchView = new PaintSplotchView(this);
        greenPaintSplotchView.setSplotchColor(Color.GREEN);
        greenPaintSplotchView.setSplotchId(1);
        mPaletteView.addView(greenPaintSplotchView);

        PaintSplotchView bluePaintSplotchView = new PaintSplotchView(this);
        bluePaintSplotchView.setSplotchColor(Color.BLUE);
        bluePaintSplotchView.setSplotchId(2);
        mPaletteView.addView(bluePaintSplotchView);

        PaintSplotchView yellowPaintSplotchView = new PaintSplotchView(this);
        yellowPaintSplotchView.setSplotchColor(Color.YELLOW);
        yellowPaintSplotchView.setSplotchId(3);
        // highlight yellow since it is the active color by default (until the user changes it)
        yellowPaintSplotchView.setHighlighted(true);
        mPaletteView.addView(yellowPaintSplotchView);

        PaintSplotchView magentaPaintSplotchView = new PaintSplotchView(this);
        magentaPaintSplotchView.setSplotchColor(Color.MAGENTA);
        magentaPaintSplotchView.setSplotchId(4);
        mPaletteView.addView(magentaPaintSplotchView);
    }

    private void createKnobSelectorView() {
        mKnobColorSelectorView = new KnobColorSelectorView(this);
        mKnobColorSelectorView.setBackgroundColor(Color.GRAY);
        mKnobColorSelectorView.setOnPlusListener(this);
        mKnobColorSelectorView.setOnMinusListener(this);
    }

    @Override
    public void onColorChanged(int color) {
        //send the color selected to the GalleryActivity
        Intent data = new Intent();
        data.putExtra(GalleryActivity.COLOR_SELECTED,color);
        setResult(Activity.RESULT_OK, data);
        savePalette();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPalette();

        if (getViews() == 0) {
           addColorsToPaletteView();
        }

    }

    @Override
    public int getViews() {
        return mPaletteView.getChildCount();
    }

    @Override
    public void addColorListener(int color) {
        mPaletteView.addColor(color);
    }

    @Override
    public void removeColorListener() {
        mPaletteView.removeColor();
    }

    public void deleteAllFiles() {
        File dir = new File(getFilesDir() + "");
        String[] files = dir.list();
        for (int i=0;i< files.length; i++) {
            if (files[i].contains(mPaletteFile)) {
                new File(dir, files[i]).delete();
            }
        }
    }

    private void savePalette() {
        try
        {
            File file = new File(getFilesDir(), mPaletteFile);
            FileWriter fileWriter = new FileWriter(file);
            int selectedChildIndex = 0;
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for(int i=0; i< mPaletteView.getChildCount(); i++) {
                PaintSplotchView childView = (PaintSplotchView) mPaletteView.getChildAt(i);
                bufferedWriter.write(childView.getSplotchColor() + "\n");
                if (childView.isHighlighted()) {
                    selectedChildIndex = i;
                }
            }
            bufferedWriter.write(selectedChildIndex + "\n");
            bufferedWriter.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void loadPalette()
    {
        //deleteAllFiles();
        try
        {
            File file = new File(getFilesDir(), mPaletteFile);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String paletteColor = bufferedReader.readLine();
            int i=0;
            do
            {
                PaintSplotchView paintSplotchView = new PaintSplotchView(this);
                paintSplotchView.setSplotchColor(Integer.parseInt(paletteColor));
                paintSplotchView.setSplotchId(i);
                mPaletteView.addView(paintSplotchView);
                i++;
            } while ((paletteColor = bufferedReader.readLine()) != null);
            // remove the last view since it is really the index of the view that is highlighted
            int highlightedIndex = ((PaintSplotchView) mPaletteView.getChildAt(i-1)).getSplotchColor();
            mPaletteView.removeView( mPaletteView.getChildAt(i-1));
            // set the child at the paletteColor index position to highlighted
            ((PaintSplotchView) mPaletteView.getChildAt(highlightedIndex)).setHighlighted(true);
            bufferedReader.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
