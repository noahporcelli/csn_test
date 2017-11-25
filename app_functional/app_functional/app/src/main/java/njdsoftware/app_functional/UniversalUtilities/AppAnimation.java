package njdsoftware.app_functional.UniversalUtilities;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

/**
 * Created by Nathan on 17/08/2016.
 */
public class AppAnimation {
    public static void fadeIn(final View view){
        final int preTime = 75;   //time before takes place - standard according to material desing specs.
        final int duration = 150;  //standard material design specs.

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setAlpha(0f);
                view.setVisibility(View.VISIBLE);
                // Animate the content view to 100% opacity, and clear any animation
                // listener set on the view.
                view.animate()
                        .alpha(1f)
                        .setDuration(duration)
                        .setListener(null);
            }
        }, preTime);
    }

    public static void fadeIn(final View view, final int preTime, final int duration){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setAlpha(0f);
                view.setVisibility(View.VISIBLE);
                // Animate the content view to 100% opacity, and clear any animation
                // listener set on the view.
                view.animate()
                        .alpha(1f)
                        .setDuration(duration)
                        .setListener(null);
            }
        }, preTime);
    }
    public static void fadeOut(final View view, final int preTime, final int duration){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setAlpha(1f);
                view.setVisibility(View.VISIBLE);
                // Animate the content view to 100% opacity, and clear any animation
                // listener set on the view.
                view.animate()
                        .alpha(0f)
                        .setDuration(duration)
                        .setListener(null);
            }
        }, preTime);
    }

    public static ObjectAnimator startCircularProgressBarAnimation(ProgressBar progressBar, int msTime, int progressBarMaxVal){
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, progressBarMaxVal); // see this max value coming back here, we animale towards that value
        animation.setDuration(msTime); //in milliseconds
        animation.setInterpolator (new LinearInterpolator());
        animation.start();
        return animation;
    }
    public static void cancelCircularProgressBarAnimation(ProgressBar progressBar){
        progressBar.clearAnimation();
    }
}
