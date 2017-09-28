package org.jeff.game24app.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import org.jeff.game24app.tiles.BaseTile;

/**
 * A class that creates an animator for a BaseTile.
 */
public class ViewAnimatorFactory {

    private View view;
    private static final int BOBBLE_DURATION = 600;
    private static final int FADE_DURATION = 200;

    public ViewAnimatorFactory(View v) {
        view = v;
    }

    /**
     * Creates an animator for a View that makes it get bigger and smaller.
     * @return an Animator object
     */
    public Animator getBobbleAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(1, 1.1f, 1);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });
        animator.setRepeatCount(Animation.INFINITE);
        animator.setDuration(BOBBLE_DURATION);
        return animator;
    }

    /**
     * Creates an animator for a View that makes it fade into view.
     * @return an Animator object
     */
    public Animator getFadeInAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setAlpha(value);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setClickable(true);
            }
        });
        animator.setDuration(FADE_DURATION);
        return animator;
    }

    /**
     * Creates an animator for a View that makes it fade out of view.
     * @return an Animator object
     */
    public Animator getFadeOutAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setAlpha(value);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setClickable(false);
            }
        });
        animator.setDuration(FADE_DURATION);
        return animator;
    }
}
