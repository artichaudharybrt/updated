package com.gamegards.gaming27._Aviator;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.gamegards.gaming27.R;

public class AviatorAnimationTestActivity extends AppCompatActivity {

    private AviatorAnimationView aviatorAnimationView;
    private TextView txt_multiplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviator_animation_test);

        // Initialize views
        aviatorAnimationView = findViewById(R.id.aviator_animation_view);
        txt_multiplier = findViewById(R.id.txt_multiplier);

        // Auto-start animation for testing
        startTestAnimation();
    }

    private void startTestAnimation() {
        // Start animation with random target multiplier between 1.5x and 5.0x
        float targetMultiplier = 1.5f + (float)(Math.random() * 3.5f);
        aviatorAnimationView.startAnimation(targetMultiplier, 9500);
    }
}
