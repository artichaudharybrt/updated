package com.gamegards.gaming27._Aviator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gamegards.gaming27.R;

public class CurveTestActivity extends Activity {
    private static final String TAG = "CurveTestActivity";

    private AviatorAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curve_test);

        initViews();
        setupAnimation();
    }

    private void initViews() {
        animationView = findViewById(R.id.aviator_animation_view);
        // Control panel removed - auto-start animation
        testCurveAnimation();
    }

    private void setupAnimation() {
        animationView.setAnimationCallback(new AviatorAnimationView.AnimationCallback() {
            @Override
            public void onAnimationStart() {
                Log.d(TAG, "Curve animation started");
            }

            @Override
            public void onAnimationUpdate(float currentMultiplier) {
                Log.d(TAG, "Animation update: " + String.format("%.2fx", currentMultiplier));
            }

            @Override
            public void onAnimationComplete(float finalMultiplier) {
                Log.d(TAG, "Curve animation completed at: " + finalMultiplier + "x");
            }

            @Override
            public void onAnimationCrash() {
                Log.d(TAG, "Curve animation crashed");
            }
        });
    }

    private void testCurveAnimation() {
        // Test different multiplier targets to see curve variations
        float[] testMultipliers = {1.5f, 2.0f, 3.5f, 5.0f, 10.0f};
        int randomIndex = (int)(Math.random() * testMultipliers.length);
        float targetMultiplier = testMultipliers[randomIndex];

        Log.d(TAG, "Testing curve animation with target: " + targetMultiplier + "x");

        // Starting curve animation

        // Start animation with 8 second duration
        animationView.startAnimation(targetMultiplier, 8000);
    }

    private void resetAnimation() {
        animationView.resetAnimation();
        Log.d(TAG, "Animation reset");
    }
}
