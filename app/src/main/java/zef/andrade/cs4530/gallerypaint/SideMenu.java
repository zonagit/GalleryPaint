package zef.andrade.cs4530.gallerypaint;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by zandrade on 9/27/2016.
 */
public class SideMenu extends LinearLayout {

    public interface OnForwardListener {
        void moveForwardListener();
    }

    public interface OnBackListener {
        void moveBackListener();
    }

    private final Button backButton;
    private final Button forwardButton;
    private final Button playButton;
    private final Button stopButton;
    private final Button resetButton;
    private final ColorControl colorControl;


    private OnForwardListener mOnForwardListener;
    private OnBackListener mOnBackListener;


    //TODO: Play/Pause Button
    //TODO: Current Color Control
    public SideMenu(Context context) {
        super(context);

        this.setOrientation(LinearLayout.HORIZONTAL);

        backButton = new Button(context);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numDrawings = Gallery.getInstance().getDrawingCount();
                int nextIndex = Gallery.getInstance().getCurrentDrawingIndex()-1;
                enableOrDisable(numDrawings,nextIndex);

                // call GalleryActivity
                mOnBackListener.moveBackListener();
            }
        });
        int size = 0;
        this.addView(backButton, new LinearLayout.LayoutParams(size, ViewGroup.LayoutParams.MATCH_PARENT,0.15f));

        forwardButton = new Button(context);

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numDrawings = Gallery.getInstance().getDrawingCount();
                int nextIndex = Gallery.getInstance().getCurrentDrawingIndex()+1;
                enableOrDisable(numDrawings,nextIndex);

                // call GalleryActivity to do the work
                mOnForwardListener.moveForwardListener();


            }
        });

        this.addView(forwardButton, new LinearLayout.LayoutParams(size, ViewGroup.LayoutParams.MATCH_PARENT,0.15f));

        handleEnableDisableOfButtons();

        playButton = new Button(context);
        playButton.setBackgroundResource(R.drawable.playbtn);
        this.addView(playButton, new LinearLayout.LayoutParams(size, ViewGroup.LayoutParams.MATCH_PARENT,0.15f));

        stopButton = new Button(context);
        stopButton.setBackgroundResource(R.drawable.stopbtn);
        this.addView(stopButton, new LinearLayout.LayoutParams(size, ViewGroup.LayoutParams.MATCH_PARENT,0.15f));

        colorControl = new ColorControl(context);
        colorControl.setSplotchColor(PaintAreaView.DEFAULT_ACTIVE_COLOR);
        this.addView(colorControl, new LinearLayout.LayoutParams(size, ViewGroup.LayoutParams.MATCH_PARENT,0.15f));

        resetButton = new Button(context);
        resetButton.setText("RESET \n (Restart\n after)");
        this.addView(resetButton, new LinearLayout.LayoutParams(size, ViewGroup.LayoutParams.MATCH_PARENT,0.20f));

    }

    public ColorControl getColorControl() {
        return colorControl;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public void handleEnableDisableOfButtons() {
        int numDrawings = Gallery.getInstance().getDrawingCount();
        int currentIndex = Gallery.getInstance().getCurrentDrawingIndex();
        enableOrDisable(numDrawings,currentIndex);
    }


    public void enableOrDisable(int numDrawings,int currentIndex) {

        if (numDrawings > 0 && currentIndex < numDrawings) {
            forwardButton.setBackgroundResource(R.drawable.forwardenabled);
            forwardButton.setEnabled(true);
        }
        else {
            forwardButton.setBackgroundResource(R.drawable.forwarddisabled);
            forwardButton.setEnabled(false);
        }
        if (numDrawings > 0 && currentIndex <= numDrawings && currentIndex > 0) {
            backButton.setBackgroundResource(R.drawable.backenabled);
            backButton.setEnabled(true);
        }
        else {
            backButton.setBackgroundResource(R.drawable.backdisabled);
            backButton.setEnabled(false);
        }
    }


    public void setOnForwardListener(OnForwardListener onForwardListener) {
        this.mOnForwardListener = onForwardListener;
    }



    public void setOnBackListener(OnBackListener onBackListener) {
        this.mOnBackListener = onBackListener;
    }

}
