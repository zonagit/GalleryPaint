package zef.andrade.cs4530.gallerypaint;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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

        this.addView(backButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

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

        this.addView(forwardButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        handleEnableDisableOfButtons();
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

    public OnForwardListener getOnForwardListener() {

        return mOnForwardListener;
    }

    public void setOnForwardListener(OnForwardListener onForwardListener) {
        this.mOnForwardListener = onForwardListener;
    }

    public OnBackListener getOnBackListener() {

        return mOnBackListener;
    }

    public void setOnBackListener(OnBackListener onBackListener) {
        this.mOnBackListener = onBackListener;
    }

}
