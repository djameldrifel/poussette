package com.theteam.zf.poussette;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created by DAOUDR on 28/08/2015.
 */
class RotatingImageView extends ImageView {
    private float angle = 0;

    public RotatingImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        //animRotate = new RotateAnimation();
    }

    public RotatingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RotatingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void rotate(float newAngle){
        this.rotate(newAngle,100);
    }

    public void rotate(float newAngle,int miliseconde){
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        final RotateAnimation animRotate = new RotateAnimation(angle, newAngle,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        angle = newAngle;

        animRotate.setDuration(miliseconde);
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);

        startAnimation(animSet);
    }
}
