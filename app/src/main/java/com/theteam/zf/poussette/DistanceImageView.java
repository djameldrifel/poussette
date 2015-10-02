package com.theteam.zf.poussette;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by DAOUDR on 01/09/2015.
 */
public class DistanceImageView extends ImageView {

    public DistanceImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.setImageResource(R.drawable.signal_none);
    }

    public DistanceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DistanceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setPourcentage(int pourcentage){
        if(pourcentage>40 || pourcentage<5){
            return;
        }
        if(pourcentage<10){
            this.setImageResource(R.drawable.signal_high);
            return;
        }
        if(pourcentage<20){
            this.setImageResource(R.drawable.signal_normal);
            return;
        }
        if(pourcentage<30){
            this.setImageResource(R.drawable.signal_low);
            return;
        }
        this.setImageResource(R.drawable.signal_none);
        return;
    }
}
