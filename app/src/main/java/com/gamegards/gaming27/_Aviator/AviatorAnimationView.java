package com.gamegards.gaming27._Aviator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import pl.droidsonroids.gif.GifDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.gamegards.gaming27.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AviatorAnimationView extends View {

    /// Animation state with precise timing
    private boolean isAnimating = false;
    private boolean isCrashed = false; // Track if rocket has crashed
    private boolean rocketContinuesMoving = false; // Rocket moves after crash
    private float crashMultiplier = 1.0f; // Multiplier when crashed
    private float currentMultiplier = 1.0f; // Internal multiplier for calculations
    private float displayMultiplier = 1.0f; // What we show to user (starts from 1.0x)
    private float targetMultiplier = 1.0f;
    private long animationDuration = 9500; // 9.5 seconds
    private boolean useBackendMultiplier = false; // Flag to use backend real-time updates
    private long lastUpdateTime = 0; // For precise timing coordination

    // Rocket properties
    private float rocketX = 0;
    private float rocketY = 0;
    private float rocketSize = 140f; // Bigger airplane for better visibility
    private float rocketRotation = 0f;
    private float wingRotation = 0f; // For wing animation
    private long animationStartTime = 0;

    // Graph properties
    private List<PointF> graphPoints = new ArrayList<>();
    private float graphStartX = 100f;
    private float graphStartY = 0;
    private float graphWidth = 0;
    private float graphHeight = 0;

    // Dynamic timeline properties - sliding window with precise smooth animation
    private int timeWindowStart = 0; // Starting time value (0s, then 1s, 2s, etc.)
    private int timeWindowSize = 6; // Always show 6 time intervals
    private int multiplierWindowStart = 0; // Starting multiplier index (for 0.5x increments)
    private int multiplierWindowSize = 8; // Show 8 multiplier intervals for better visibility

    // Smooth sliding animation properties
    private float timeSlideOffset = 0f; // Smooth sliding offset for time labels
    private float multiplierSlideOffset = 0f; // Smooth sliding offset for multiplier labels
    private int targetTimeWindowStart = 0; // Target position for smooth transition
    private int targetMultiplierWindowStart = 0; // Target position for smooth transition

    // Particle system for exhaust trail
    private List<Particle> particles = new ArrayList<>();
    private List<Star> stars = new ArrayList<>();
    private List<Cloud> clouds = new ArrayList<>();
    private List<TrailDot> trailDots = new ArrayList<>(); // New dot-based trail system
    private List<RocketDebris> rocketDebris = new ArrayList<>(); // Rocket pieces
    private Random random = new Random();

    // Enhanced visual effects
    private float screenShakeX = 0f;
    private float screenShakeY = 0f;
    private boolean isShaking = false;
    private int shakeFrames = 0;

    // Edge animation variables
    private boolean isAtEdge = false;
    private float edgeAnimationOffset = 0f;
    private long edgeAnimationStartTime = 0;
    private float edgeAnimationSpeed = 0.05f; // Speed of up/down animation

    // Cinematic movement illusion variables
    private boolean cinematicMode = false; // When rocket reaches center, enable parallax
    private float backgroundScrollOffset = 0f; // How much background has scrolled
    private float rocketCenterX = 0f; // X position where rocket stops (center screen)
    private float scrollSpeed = 2f; // Speed of background scrolling

    // Curve parameters for realistic flight path
    private float curveVariation = 0f;
    private float turbulence = 0f;

    // Paint objects
    private Paint rocketPaint;
    private Paint graphPaint;
    private Paint backgroundPaint;
    private Paint textPaint;
    private Paint particlePaint;
    private Paint gridPaint;
    private Paint starPaint;
    private Paint cloudPaint;
    private Paint trailPaint;

    // Drawable resources
    private GifDrawable rocketDrawable;

    // Animation
    private ValueAnimator rocketAnimator;
    private Runnable crashFallbackRunnable; // Fallback crash timer

    // Callback interface for animation events
    public interface AnimationCallback {
        void onAnimationStart();
        void onAnimationUpdate(float currentMultiplier);
        void onAnimationComplete(float finalMultiplier);
        void onAnimationCrash();
    }

    private AnimationCallback animationCallback;

    public AviatorAnimationView(Context context) {
        super(context);
        init();
    }

    public AviatorAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AviatorAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Initialize paint objects
        rocketPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rocketPaint.setColor(Color.WHITE);
        rocketPaint.setStyle(Paint.Style.FILL);

        graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        graphPaint.setColor(Color.RED); // Red color like reference image
        graphPaint.setStrokeWidth(6f); // Slightly thicker
        graphPaint.setStyle(Paint.Style.STROKE);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.BLACK);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(48f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        particlePaint.setColor(Color.YELLOW);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(1f);
        gridPaint.setAlpha(100);

        starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setColor(Color.LTGRAY);
        starPaint.setStyle(Paint.Style.FILL);

        cloudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cloudPaint.setColor(Color.parseColor("#CCCCCC"));
        cloudPaint.setAlpha(80);

        trailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trailPaint.setColor(Color.parseColor("#FFD700"));
        trailPaint.setStrokeWidth(3f);
        trailPaint.setStyle(Paint.Style.STROKE);

        // Load aviator airplane GIF
        try {
            rocketDrawable = new GifDrawable(getContext().getResources(), R.drawable.aviator);
        } catch (Exception e) {
        }

        // Initialize background elements
        initializeStars();
        initializeClouds();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate graph dimensions - ensure time labels are visible above betting panel
        graphStartX = 60f; // Further reduced left margin for Y-axis labels
        graphWidth = w - 80f; // Adjust for left margin
        graphHeight = h * 0.6f; // Use 60% of screen height for graph
        graphStartY = h * 0.75f; // Position X-axis at 75% of screen height, leaving space for time labels

        // Set initial rocket position
        rocketX = graphStartX;
        rocketY = graphStartY;

        // Create gradient background
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, h,
                new int[]{Color.parseColor("#001122"), Color.parseColor("#000000")},
                null, Shader.TileMode.CLAMP
        );
        backgroundPaint.setShader(gradient);

        // Reposition stars and clouds for new dimensions
        for (Star star : stars) {
            if (star.x > w) star.x = random.nextFloat() * w;
            if (star.y > h) star.y = random.nextFloat() * h;
        }

        for (Cloud cloud : clouds) {
            if (cloud.x > w) cloud.x = random.nextFloat() * w;
            if (cloud.y > h) cloud.y = random.nextFloat() * h;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Apply screen shake effect
        if (isShaking) {
            canvas.save();
            canvas.translate(screenShakeX, screenShakeY);
        }

        // Draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        // Draw background elements
        drawStars(canvas);
        drawClouds(canvas);

        // Draw grid
        drawGrid(canvas);

        // Draw graph line FIRST
        drawGraph(canvas);

        // Draw rocket trail
        drawRocketTrail(canvas);

        // Draw rocket LAST
        drawRocket(canvas);

        // Draw particles (exhaust trail) - some affected by parallax, some not
        drawParticles(canvas);

        // Draw rocket debris
        drawRocketDebris(canvas);

        // Draw multiplier text (not affected by parallax)
        drawMultiplierText(canvas);

        // Update all elements
        updateParticles();
        updateStars();
        updateClouds();
        updateRocketDebris();
        updateScreenShake();

        // Restore canvas if shaking
        if (isShaking) {
            canvas.restore();
        }

        // Continue animation if running or rocket continues moving
        if (isAnimating || rocketContinuesMoving) {
            invalidate();
        }
    }

    private void drawGrid(Canvas canvas) {
        // Smooth synchronized animation for both timer and multiplier
        if (isAnimating && animationStartTime > 0) {
            long currentTime = System.currentTimeMillis();
            float elapsedSeconds = (currentTime - animationStartTime) / 1000f;
            Log.e("jvgfuydv", String.valueOf(elapsedSeconds));

            // Smooth timer sliding based on elapsed time (opposite direction to multiplier)
            float targetTimeOffset = elapsedSeconds / 0.5f; // Even faster for full width coverage
            timeSlideOffset = -(targetTimeOffset - (int)targetTimeOffset); // Opposite direction
            timeWindowStart = (int)targetTimeOffset;

            // Smooth multiplier sliding based on current multiplier (upward like rocket)
            float multiplierProgress = (currentMultiplier - 1.0f) / 0.5f; // 0.5x steps
            multiplierSlideOffset = multiplierProgress - (int)multiplierProgress; // Fractional part
            multiplierWindowStart = (int)multiplierProgress;
        }

        // Draw vertical grid lines covering full width
        int totalTimeDots = 20; // More dots to cover full width
        for (int i = -1; i <= totalTimeDots; i++) {
            int timeValue = timeWindowStart + i;
            if (timeValue < 0 || timeValue > 60) continue;

            // Apply sliding offset covering full graph width
            float x = graphStartX + ((i - timeSlideOffset) * graphWidth / (totalTimeDots - 1));

            // Draw grid lines for visible positions
            if (i >= 0 && i < totalTimeDots) {
                canvas.drawLine(x, graphStartY, x, graphStartY - graphHeight, gridPaint);
            }

            // Draw time labels as dots
            String timeLabel = "•";

            Paint timeLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            timeLabelPaint.setTextSize(38f); // Slightly smaller for better fit
            timeLabelPaint.setColor(Color.WHITE);
            timeLabelPaint.setTextAlign(Paint.Align.CENTER);
            timeLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
            timeLabelPaint.setShadowLayer(4f, 2f, 2f, Color.BLACK);

            // Precise alpha calculation for smooth fading
            float alpha = 1.0f;
            if (i < 0) alpha = Math.max(0f, 1.0f + i + timeSlideOffset);
            if (i >= totalTimeDots) alpha = Math.max(0f, totalTimeDots - i + timeSlideOffset);
            timeLabelPaint.setAlpha((int)(alpha * 255));

            float labelY = graphStartY + 25f;
            canvas.drawText(timeLabel, x, labelY, timeLabelPaint);

            // Background with precise alpha
            Paint labelBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            labelBgPaint.setColor(Color.parseColor("#333333"));
            labelBgPaint.setAlpha((int)(100 * alpha));
            Rect textBounds = new Rect();
            timeLabelPaint.getTextBounds(timeLabel, 0, timeLabel.length(), textBounds);
            canvas.drawRoundRect(
                    x - textBounds.width()/2 - 8f,
                    labelY - textBounds.height() - 4f,
                    x + textBounds.width()/2 + 8f,
                    labelY + 4f,
                    8f, 8f, labelBgPaint
            );
            canvas.drawText(timeLabel, x, labelY, timeLabelPaint);
        }

        // Draw horizontal grid lines with precise BOTTOM TO TOP sliding
        for (int i = -1; i <= multiplierWindowSize; i++) {
            // Apply precise sliding offset to Y position
            float y = graphStartY - ((i + multiplierSlideOffset) * graphHeight / (multiplierWindowSize - 1));

            // Draw grid lines for visible positions
            if (i >= 0 && i < multiplierWindowSize) {
                canvas.drawLine(graphStartX, y, graphStartX + graphWidth, y, gridPaint);
            }

            // Calculate multiplier values with consistent 0.5x increments for better visibility
            float multiplierStep = 0.5f; // Consistent with window calculation
            int multiplierIndex = multiplierWindowStart + i;
            if (multiplierIndex < 0) continue;

            float baseMultiplier = 1.0f + multiplierIndex * multiplierStep;
            baseMultiplier = Math.min(baseMultiplier, 50.0f); // Allow higher maximum for better scaling

            // Draw multiplier labels as dots
            if (baseMultiplier >= 1.0f && baseMultiplier <= 50.0f) {
                String multiplierLabel = "•";

                Paint multiplierLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                multiplierLabelPaint.setTextSize(36f); // Larger text for better visibility
                multiplierLabelPaint.setColor(Color.WHITE);
                multiplierLabelPaint.setTextAlign(Paint.Align.RIGHT);
                multiplierLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
                multiplierLabelPaint.setShadowLayer(4f, 2f, 2f, Color.BLACK); // Stronger shadow

                // Improved alpha calculation for smoother transitions
                float alpha = 1.0f;
                if (i < 0) alpha = Math.max(0f, 1.0f + i - multiplierSlideOffset);
                if (i >= multiplierWindowSize) alpha = Math.max(0f, multiplierWindowSize - i + multiplierSlideOffset);

                // Ensure minimum visibility for current range
                if (alpha < 0.3f && i >= 0 && i < multiplierWindowSize) {
                    alpha = 0.3f; // Minimum visibility for grid labels
                }

                multiplierLabelPaint.setAlpha((int)(alpha * 255));

                // Draw background for better readability
                Rect textBounds = new Rect();
                multiplierLabelPaint.getTextBounds(multiplierLabel, 0, multiplierLabel.length(), textBounds);

                Paint labelBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                labelBgPaint.setColor(Color.parseColor("#1A1A1A"));
                labelBgPaint.setAlpha((int)(150 * alpha));
                canvas.drawRoundRect(
                        graphStartX - textBounds.width() - 35f,
                        y - textBounds.height()/2 - 5f,
                        graphStartX - 15f,
                        y + textBounds.height()/2 + 5f,
                        8f, 8f, labelBgPaint
                );

                canvas.drawText(multiplierLabel, graphStartX - 15f, y + 12f, multiplierLabelPaint);
            }
        }

        // Draw axes - make them more prominent
        Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.WHITE);
        axisPaint.setStrokeWidth(4f);

        // X-axis
        canvas.drawLine(graphStartX, graphStartY, graphStartX + graphWidth, graphStartY, axisPaint);

        // Y-axis
        canvas.drawLine(graphStartX, graphStartY, graphStartX, graphStartY - graphHeight, axisPaint);
    }

    private void drawGraph(Canvas canvas) {
        if (graphPoints.size() < 2) return;

        // Create ultra-smooth curve path using Catmull-Rom splines for wind-like fluidity
        Path graphPath = new Path();
        graphPath.moveTo(graphStartX, graphStartY);

        // Generate smooth curve points using Catmull-Rom interpolation
        List<PointF> smoothPoints = generateCatmullRomCurve(graphPoints);

        // Create the fluid, wind-drawn curve
        for (int i = 0; i < smoothPoints.size(); i++) {
            PointF point = smoothPoints.get(i);
            float animatedY = point.y;

            // No turbulence - smooth curve only

            if (isAtEdge && i >= smoothPoints.size() - 10) {
                animatedY += edgeAnimationOffset * 0.3f;
            }

            if (i == 0) {
                graphPath.lineTo(point.x, animatedY);
            } else {
                // Use cubic bezier for ultra-smooth curves
                PointF prevPoint = smoothPoints.get(i - 1);
                float prevY = prevPoint.y;

                if (isAtEdge && i - 1 >= smoothPoints.size() - 10) {
                    prevY += edgeAnimationOffset * 0.3f;
                }

                // Calculate control points for cubic bezier (creates flowing curves)
                float controlDistance = Math.abs(point.x - prevPoint.x) * 0.4f;
                float cp1X = prevPoint.x + controlDistance;
                float cp1Y = prevY;
                float cp2X = point.x - controlDistance;
                float cp2Y = animatedY;

                graphPath.cubicTo(cp1X, cp1Y, cp2X, cp2Y, point.x, animatedY);
            }
        }

        // Draw RED EXHAUST AREA below the curve using same smooth curve
        Path redAreaPath = new Path();
        redAreaPath.moveTo(graphStartX, graphStartY); // Start from origin

        // Use the same smooth curve points for consistent visual
        for (int i = 0; i < smoothPoints.size(); i++) {
            PointF point = smoothPoints.get(i);
            float animatedY = point.y;

            // No turbulence - smooth curve only

            // Apply edge animation to the last few points if at edge
            if (isAtEdge && i >= smoothPoints.size() - 10) {
                animatedY += edgeAnimationOffset * 0.3f;
            }

            if (i == 0) {
                redAreaPath.lineTo(point.x, animatedY);
            } else {
                // Use same cubic bezier technique for area consistency
                PointF prevPoint = smoothPoints.get(i - 1);
                float prevY = prevPoint.y;

                if (isAtEdge && i - 1 >= smoothPoints.size() - 10) {
                    prevY += edgeAnimationOffset * 0.3f;
                }

                float controlDistance = Math.abs(point.x - prevPoint.x) * 0.4f;
                float cp1X = prevPoint.x + controlDistance;
                float cp1Y = prevY;
                float cp2X = point.x - controlDistance;
                float cp2Y = animatedY;

                redAreaPath.cubicTo(cp1X, cp1Y, cp2X, cp2Y, point.x, animatedY);

                if (i == smoothPoints.size() - 1) {
                    redAreaPath.lineTo(point.x, graphStartY);
                    redAreaPath.lineTo(graphStartX, graphStartY);
                }
            }
        }
        redAreaPath.close();

        // Fill the area below curve with red gradient - always from origin
        Paint redAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float topY = graphPoints.size() > 0 ? graphPoints.get(graphPoints.size() - 1).y : graphStartY;
        LinearGradient redGradient = new LinearGradient(
                0, topY, 0, graphStartY,
                new int[]{Color.parseColor("#FF0000"), Color.parseColor("#AA0000"), Color.parseColor("#660000")},
                new float[]{0f, 0.6f, 1f},
                Shader.TileMode.CLAMP
        );
        redAreaPaint.setShader(redGradient);
        redAreaPaint.setAlpha(100); // More translucent (was 180)
        canvas.drawPath(redAreaPath, redAreaPaint);

        // Draw the main curve line on top
        canvas.drawPath(graphPath, graphPaint);

        // Add glow effect to make it more visible like in Aviator
        Paint glowPaint = new Paint(graphPaint);
        glowPaint.setStrokeWidth(graphPaint.getStrokeWidth() + 4f);
        glowPaint.setAlpha(80);
        canvas.drawPath(graphPath, glowPaint);
    }

    private void drawRocket(Canvas canvas) {
        // Check if rocket is visible on screen - if not, don't draw but continue animation
        float adjustedX = rocketX + 8f;
        float adjustedY = rocketY - 12f;

        // Allow some margin for partial visibility
        boolean isVisible = adjustedX > -rocketSize && adjustedX < getWidth() + rocketSize &&
                adjustedY > -rocketSize && adjustedY < getHeight() + rocketSize;

        if (!isVisible) {
            return; // Don't draw but continue animation
        }

        canvas.save();
        canvas.translate(adjustedX, adjustedY);
        canvas.rotate(rocketRotation);

        // Update wing rotation for flying animation
        long currentTime = System.currentTimeMillis();
        if (animationStartTime == 0) {
            animationStartTime = currentTime;
        }
        wingRotation = (float) Math.sin((currentTime - animationStartTime) * 0.01f) * 15f; // Oscillate wings

        if (rocketDrawable != null) {
            // Use the aviator.png image with better alignment
            int halfSize = (int)(rocketSize / 2);

            // Adjust bounds for better curve alignment (bigger airplane)
            int adjustedWidth = (int)(rocketSize * 0.9f); // Good width for bigger plane
            int adjustedHeight = (int)(rocketSize * 0.7f); // Good height for bigger plane

            rocketDrawable.setBounds(-adjustedWidth/2, -adjustedHeight/2, adjustedWidth/2, adjustedHeight/2);
            rocketDrawable.draw(canvas);
        } else {
            // Fallback: Draw simple airplane shape if image fails to load
            drawFallbackAirplane(canvas);
        }

        canvas.restore();
    }

    private void drawFallbackAirplane(Canvas canvas) {
        // Simple fallback airplane if image fails to load
        Paint airplanePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        airplanePaint.setColor(Color.parseColor("#E91E63")); // Aviator pink-red
        airplanePaint.setStyle(Paint.Style.FILL);

        float scale = rocketSize / 140f;

        // Simple airplane body
        Path fuselage = new Path();
        fuselage.moveTo(60f * scale, 0);
        fuselage.lineTo(40f * scale, -8f * scale);
        fuselage.lineTo(-40f * scale, -6f * scale);
        fuselage.lineTo(-60f * scale, 0);
        fuselage.lineTo(-40f * scale, 6f * scale);
        fuselage.lineTo(40f * scale, 8f * scale);
        fuselage.close();
        canvas.drawPath(fuselage, airplanePaint);

        // Simple wings
        Path wing1 = new Path();
        wing1.moveTo(10f * scale, -8f * scale);
        wing1.lineTo(40f * scale, -30f * scale);
        wing1.lineTo(30f * scale, -35f * scale);
        wing1.lineTo(0f * scale, -15f * scale);
        wing1.close();
        canvas.drawPath(wing1, airplanePaint);

        Path wing2 = new Path();
        wing2.moveTo(10f * scale, 8f * scale);
        wing2.lineTo(40f * scale, 30f * scale);
        wing2.lineTo(30f * scale, 35f * scale);
        wing2.lineTo(0f * scale, 15f * scale);
        wing2.close();
        canvas.drawPath(wing2, airplanePaint);

    }

    private void drawParticles(Canvas canvas) {
        for (Particle particle : particles) {
            particlePaint.setColor(particle.color);
            particlePaint.setAlpha((int)(particle.alpha * 255));
            canvas.drawCircle(particle.x, particle.y, particle.size, particlePaint);

            // Add glow effect for larger particles
            if (particle.size > 10) {
                particlePaint.setAlpha((int)(particle.alpha * 100));
                canvas.drawCircle(particle.x, particle.y, particle.size * 1.5f, particlePaint);
            }
        }
    }

    private void drawMultiplierText(Canvas canvas) {
        // Multiplier display completely removed from animation view
        // Pure visual focus - no numbers, no timers, just flight experience
        // All control logic handled by backend only
    }

    private void drawStars(Canvas canvas) {
        for (Star star : stars) {
            starPaint.setAlpha((int)(star.alpha * 255));
            canvas.drawCircle(star.x, star.y, star.size, starPaint);
        }
    }

    private void drawClouds(Canvas canvas) {
        for (Cloud cloud : clouds) {
            if (cloud.isExplosionCloud) {
                // Grey explosion clouds
                cloudPaint.setColor(Color.parseColor("#888888"));
                cloudPaint.setAlpha((int)(cloud.alpha * 120));
            } else {
                // Normal background clouds
                cloudPaint.setColor(Color.parseColor("#CCCCCC"));
                cloudPaint.setAlpha((int)(cloud.alpha * 80));
            }

            canvas.drawCircle(cloud.x, cloud.y, cloud.size, cloudPaint);
            canvas.drawCircle(cloud.x + cloud.size * 0.7f, cloud.y, cloud.size * 0.8f, cloudPaint);
            canvas.drawCircle(cloud.x - cloud.size * 0.7f, cloud.y, cloud.size * 0.8f, cloudPaint);
        }
    }

    private void drawRocketTrail(Canvas canvas) {
        // Generate trail dots along the smooth curve path - stardust effect
        if (isAnimating && graphPoints.size() > 1) {
            generateTrailDots();
        }

        // Draw all trail dots with pulsing, glowing effects
        drawTrailDots(canvas);

        // Update and clean up trail dots
        updateTrailDots();
    }

    /**
     * Generate new trail dots along the rocket's path - like stardust with cinematic effect
     */
    private void generateTrailDots() {
        if (graphPoints.size() < 2) return;

        // In cinematic mode, generate dots at rocket position (center screen)
        if (cinematicMode) {
            // Generate dots at rocket's current position
            for (int j = 0; j < 5; j++) { // More dots in cinematic mode
                // Random offset for organic spread
                float offsetX = (float) (Math.random() - 0.5) * 15f;
                float offsetY = (float) (Math.random() - 0.5) * 15f;

                float dotX = rocketX + offsetX;
                float dotY = rocketY + offsetY;

                // Larger, more dramatic dots in cinematic mode
                float dotSize = 3f + (float) Math.random() * 8f;

                // Brighter colors for cinematic effect
                int color = getCinematicTrailDotColor();

                TrailDot dot = new TrailDot(dotX, dotY, dotSize, color);
                trailDots.add(dot);
            }
        } else {
            // Normal mode - dots along curve path
            int recentPoints = Math.min(8, graphPoints.size());
            for (int i = Math.max(0, graphPoints.size() - recentPoints); i < graphPoints.size(); i++) {
                PointF point = graphPoints.get(i);

                // Add multiple dots per point for density
                for (int j = 0; j < 3; j++) {
                    // Random offset for organic spread
                    float offsetX = (float) (Math.random() - 0.5) * 12f;
                    float offsetY = (float) (Math.random() - 0.5) * 12f;

                    float dotX = point.x + offsetX;
                    float dotY = point.y + offsetY;

                    // Varied dot sizes for organic feel
                    float dotSize = 2f + (float) Math.random() * 6f;

                    // Color based on position in trail and curve steepness
                    int color = getTrailDotColor(i, graphPoints.size());

                    TrailDot dot = new TrailDot(dotX, dotY, dotSize, color);
                    trailDots.add(dot);
                }
            }
        }

        // Limit total dots for performance
        while (trailDots.size() > 400) { // More dots allowed in cinematic mode
            trailDots.remove(0);
        }
    }

    /**
     * Get cinematic trail dot colors - brighter and more dramatic
     */
    private int getCinematicTrailDotColor() {
        float colorChoice = (float) Math.random();
        if (colorChoice < 0.3f) {
            return Color.parseColor("#FFFFFF"); // Bright white
        } else if (colorChoice < 0.6f) {
            return Color.parseColor("#FFD700"); // Gold
        } else if (colorChoice < 0.8f) {
            return Color.parseColor("#00FFFF"); // Cyan
        } else {
            return Color.parseColor("#FF69B4"); // Hot pink
        }
    }

    /**
     * Get color for trail dot based on position and curve characteristics
     */
    private int getTrailDotColor(int pointIndex, int totalPoints) {
        // Calculate position in trail (0.0 = oldest, 1.0 = newest)
        float trailPosition = (float) pointIndex / totalPoints;

        // Color progression from red (old) to bright white/gold (new)
        if (trailPosition < 0.3f) {
            return Color.parseColor("#FF4444"); // Red for older parts
        } else if (trailPosition < 0.6f) {
            return Color.parseColor("#FF8800"); // Orange for middle
        } else if (trailPosition < 0.8f) {
            return Color.parseColor("#FFD700"); // Gold for recent
        } else {
            return Color.parseColor("#FFFFFF"); // Bright white for newest
        }
    }

    /**
     * Draw all trail dots with pulsing and glowing effects
     */
    private void drawTrailDots(Canvas canvas) {
        Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);

        for (TrailDot dot : trailDots) {
            // Set color and alpha
            dotPaint.setColor(dot.color);
            dotPaint.setAlpha((int) (dot.alpha * 255));

            // Draw main dot
            canvas.drawCircle(dot.x, dot.y, dot.size, dotPaint);

            // Add glow effect for special dots
            if (dot.isGlowing) {
                dotPaint.setAlpha((int) (dot.alpha * dot.glowIntensity * 100));
                canvas.drawCircle(dot.x, dot.y, dot.size * 2.5f, dotPaint);

                // Inner bright core
                dotPaint.setColor(Color.WHITE);
                dotPaint.setAlpha((int) (dot.alpha * 200));
                canvas.drawCircle(dot.x, dot.y, dot.size * 0.6f, dotPaint);
            }
        }
    }

    /**
     * Update trail dots - handle pulsing, fading, scrolling, and cleanup
     */
    private void updateTrailDots() {
        for (int i = trailDots.size() - 1; i >= 0; i--) {
            TrailDot dot = trailDots.get(i);
            dot.update();

            // In cinematic mode, dots scroll left with background
            if (cinematicMode) {
                dot.x -= scrollSpeed * 0.8f; // Slightly slower than background for depth

                // Remove dots that have scrolled off screen
                if (dot.x < -50f) {
                    trailDots.remove(i);
                    continue;
                }
            }

            if (dot.isDead()) {
                trailDots.remove(i);
            }
        }
    }

    private void updateParticles() {
        // Add new particles at rocket exhaust position - like reference image
        if (isAnimating && particles.size() < 100) {
            // Create multiple particles per frame for better effect
            for (int j = 0; j < 4; j++) {
                Particle particle = new Particle();
                // Position particles at rocket exhaust area - below the rocket
                particle.x = rocketX + random.nextFloat() * 20 - 10f;
                particle.y = rocketY + rocketSize/2 + random.nextFloat() * 15; // Start from exhaust area
                particle.vx = random.nextFloat() * 8 - 4;
                particle.vy = random.nextFloat() * 6 + 3; // Move downward/backward more
                particle.size = random.nextFloat() * 15 + 5; // Bigger particles
                particle.alpha = 1.0f;
                particle.life = 50 + random.nextInt(25);

                // More realistic exhaust colors like reference - more red
                float colorChoice = random.nextFloat();
                if (colorChoice < 0.5f) {
                    particle.color = Color.parseColor("#FF0000"); // Bright Red
                } else if (colorChoice < 0.7f) {
                    particle.color = Color.parseColor("#FF4444"); // Red
                } else if (colorChoice < 0.85f) {
                    particle.color = Color.parseColor("#FF8800"); // Orange
                } else if (colorChoice < 0.95f) {
                    particle.color = Color.parseColor("#FFDD00"); // Yellow
                } else {
                    particle.color = Color.parseColor("#FFFFFF"); // White hot
                }

                particles.add(particle);
            }
        }

        // Update existing particles
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            particle.x += particle.vx;
            particle.y += particle.vy;
            particle.alpha -= 0.025f; // Fade faster
            particle.life--;

            // Add gravity effect to particles
            particle.vy += 0.1f;

            if (particle.alpha <= 0 || particle.life <= 0) {
                particles.remove(i);
            }
        }
    }

    private void drawRocketDebris(Canvas canvas) {
        Paint debrisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        for (RocketDebris debris : rocketDebris) {
            canvas.save();
            canvas.translate(debris.x, debris.y);
            canvas.rotate(debris.rotation);

            debrisPaint.setColor(debris.color);
            debrisPaint.setAlpha((int)(debris.alpha * 255));

            // Draw rectangular debris piece
            canvas.drawRect(-debris.size/2, -debris.size/4, debris.size/2, debris.size/4, debrisPaint);

            canvas.restore();
        }
    }

    private void updateRocketDebris() {
        for (int i = rocketDebris.size() - 1; i >= 0; i--) {
            RocketDebris debris = rocketDebris.get(i);
            debris.update();

            if (debris.isDead()) {
                rocketDebris.remove(i);
            }
        }
    }

    private void initializeStars() {
        stars.clear();
        for (int i = 0; i < 50; i++) {
            Star star = new Star();
            star.x = random.nextFloat() * 1000; // Will be adjusted in onSizeChanged
            star.y = random.nextFloat() * 1000;
            star.size = random.nextFloat() * 3 + 1;
            star.alpha = random.nextFloat() * 0.8f + 0.2f;
            star.twinkleSpeed = random.nextFloat() * 0.02f + 0.01f;
            stars.add(star);
        }
    }

    private void initializeClouds() {
        clouds.clear();
        for (int i = 0; i < 8; i++) {
            Cloud cloud = new Cloud();
            cloud.x = random.nextFloat() * 1200; // Will be adjusted in onSizeChanged
            cloud.y = random.nextFloat() * 800;
            cloud.size = random.nextFloat() * 40 + 20;
            cloud.alpha = random.nextFloat() * 0.6f + 0.2f;
            cloud.speedX = random.nextFloat() * 0.5f + 0.2f;
            clouds.add(cloud);
        }
    }

    private void updateStars() {
        for (int i = stars.size() - 1; i >= 0; i--) {
            Star star = stars.get(i);

            if (star.isExplosionStar) {
                // Fade out explosion stars
                star.alpha += star.twinkleSpeed;

                // Remove after 5 seconds or fully faded
                long age = System.currentTimeMillis() - star.creationTime;
                if (age > 5000 || star.alpha <= 0) {
                    stars.remove(i);
                    continue;
                }
            } else {
                // Normal twinkling stars
                star.alpha += star.twinkleSpeed;
                if (star.alpha > 1.0f || star.alpha < 0.2f) {
                    star.twinkleSpeed = -star.twinkleSpeed;
                }
                star.alpha = Math.max(0.2f, Math.min(1.0f, star.alpha));

                // Moving background effect when animating
                if (isAnimating) {
                    star.x -= 0.5f;
                    if (star.x < -50) {
                        star.x = getWidth() + 50;
                        star.y = random.nextFloat() * getHeight();
                    }
                }
            }
        }
    }

    private void updateClouds() {
        for (int i = clouds.size() - 1; i >= 0; i--) {
            Cloud cloud = clouds.get(i);

            // Handle explosion clouds differently
            if (cloud.isExplosionCloud) {
                // Fade out explosion clouds over 10 seconds
                cloud.alpha -= cloud.fadeSpeed;
                cloud.x += cloud.speedX * 0.5f; // Slow drift

                // Remove after 5 seconds or fully faded
                long age = System.currentTimeMillis() - cloud.creationTime;
                if (age > 5000 || cloud.alpha <= 0) {
                    clouds.remove(i);
                }
            } else {
                // Normal background clouds
                if (isAnimating) {
                    cloud.speedX = Math.max(cloud.speedX, 1.0f);
                }
                cloud.x += cloud.speedX;
                if (cloud.x > getWidth() + cloud.size) {
                    cloud.x = -cloud.size;
                    cloud.y = random.nextFloat() * getHeight();
                }
            }
        }
    }

    private void updateScreenShake() {
        if (isShaking) {
            shakeFrames--;
            if (shakeFrames <= 0) {
                isShaking = false;
                screenShakeX = 0;
                screenShakeY = 0;
            } else {
                float intensity = shakeFrames / 30f * 10f;
                screenShakeX = (random.nextFloat() - 0.5f) * intensity;
                screenShakeY = (random.nextFloat() - 0.5f) * intensity;
            }
        }
    }

    private void startScreenShake() {
        isShaking = true;
        shakeFrames = 60; // 1 second at 60fps for stronger effect
    }

    private void updateEdgeAnimation() {
        if (isAtEdge && edgeAnimationStartTime > 0) {
            long currentTime = System.currentTimeMillis();
            float elapsedTime = (currentTime - edgeAnimationStartTime) * edgeAnimationSpeed;

            // Simple up and down animation using sine wave
            edgeAnimationOffset = (float) Math.sin(elapsedTime) * 15f; // 15 pixels up/down movement

        } else {
            edgeAnimationOffset = 0f;
        }
    }

    /**
     * Generate ultra-smooth Catmull-Rom curve points for wind-like fluidity
     */
    private List<PointF> generateCatmullRomCurve(List<PointF> controlPoints) {
        List<PointF> smoothPoints = new ArrayList<>();

        if (controlPoints.size() < 2) {
            return new ArrayList<>(controlPoints);
        }

        // Add first point
        smoothPoints.add(new PointF(controlPoints.get(0).x, controlPoints.get(0).y));

        // Generate smooth interpolated points between each pair of control points
        for (int i = 0; i < controlPoints.size() - 1; i++) {
            PointF p0 = i > 0 ? controlPoints.get(i - 1) : controlPoints.get(i);
            PointF p1 = controlPoints.get(i);
            PointF p2 = controlPoints.get(i + 1);
            PointF p3 = i < controlPoints.size() - 2 ? controlPoints.get(i + 2) : controlPoints.get(i + 1);

            // Generate multiple interpolated points for ultra-smooth curve
            int segments = 12; // More segments = smoother curve (increased for better quality)
            for (int t = 1; t <= segments; t++) {
                float u = (float) t / segments;
                PointF interpolated = catmullRomInterpolate(p0, p1, p2, p3, u);
                smoothPoints.add(interpolated);
            }
        }

        return smoothPoints;
    }

    /**
     * Catmull-Rom spline interpolation for smooth curves without bumps
     */
    private PointF catmullRomInterpolate(PointF p0, PointF p1, PointF p2, PointF p3, float t) {
        float t2 = t * t;
        float t3 = t2 * t;

        // Catmull-Rom formula for smooth interpolation
        float x = 0.5f * ((2 * p1.x) +
                (-p0.x + p2.x) * t +
                (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t2 +
                (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t3);

        float y = 0.5f * ((2 * p1.y) +
                (-p0.y + p2.y) * t +
                (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t2 +
                (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * t3);

        return new PointF(x, y);
    }

    private float calculateCurveFactor(float targetMultiplier) {

        if (targetMultiplier <= 1.5f) {
            return 0.8f; // Very gentle for small multipliers
        } else if (targetMultiplier <= 3.0f) {
            return 1.0f; // Gentle curve
        } else if (targetMultiplier <= 10.0f) {
            return 1.2f; // Medium curve
        } else if (targetMultiplier <= 20.0f) {
            return 1.4f; // Steeper for high multipliers
        } else {
            return 1.6f; // Steep for extreme multipliers
        }
    }

    // Public methods for controlling animation
    public void startAnimation(float targetMultiplier, long duration) {
        // Set target with reasonable maximum
        this.targetMultiplier = Math.min(targetMultiplier, 20.0f);
        this.animationDuration = duration;
        this.isAnimating = true;

        // Initialize precise timing
        animationStartTime = System.currentTimeMillis();
        wingRotation = 0f;

        // Reset sliding windows with precise initial values
        timeWindowStart = 0;
        multiplierWindowStart = 0; // Start showing 1.0x-1.6x range for better precision
        timeSlideOffset = 0f;
        multiplierSlideOffset = 0f;
        targetTimeWindowStart = 0;
        targetMultiplierWindowStart = 0;

        // Initialize graph with origin point
        graphPoints.clear();
        graphPoints.add(new PointF(graphStartX, graphStartY));

        // Reset rocket with precise positioning
        rocketX = graphStartX;
        rocketY = graphStartY;
        currentMultiplier = 1.0f;
        displayMultiplier = 1.0f;

        // Start appropriate animation mode
        if (!useBackendMultiplier) {
            startRocketAnimation();
        } else {
        }

        // Trigger callback
        if (animationCallback != null) {
            animationCallback.onAnimationStart();
        }

    }

    public void stopAnimation() {
        isAnimating = false;
        if (rocketAnimator != null) {
            rocketAnimator.cancel();
        }
        particles.clear();
    }

    public float getCurrentMultiplier() {
        return currentMultiplier;
    }

    public void setAnimationCallback(AnimationCallback callback) {
        this.animationCallback = callback;
    }

    // Method to update multiplier from backend in real-time with precise timing
    public void updateMultiplierFromBackend(float backendMultiplier) {
        if (isAnimating && useBackendMultiplier) {
            // Store the backend multiplier with precise timing
            currentMultiplier = backendMultiplier;
            displayMultiplier = backendMultiplier;

            // Update rocket position with precise coordination
            updateRocketPositionFromMultiplierPrecise(backendMultiplier);

            // Update sliding windows based on current multiplier for better coordination
            updateSlidingWindowsFromMultiplier(backendMultiplier);

            // Trigger callback
            if (animationCallback != null) {
                animationCallback.onAnimationUpdate(backendMultiplier);
            }

            // Redraw immediately for precise coordination
            invalidate();

        }
    }

    // Method to enable/disable backend multiplier mode
    public void setUseBackendMultiplier(boolean useBackend) {
        this.useBackendMultiplier = useBackend;
    }

    // Method to get animation start time for smooth crash timing
    public long getAnimationStartTime() {
        return animationStartTime;
    }

    // Independent rocket movement - continues after crash
    private void updateRocketPositionFromMultiplierPrecise(float multiplier) {
        long currentTime = System.currentTimeMillis();
        float elapsedSeconds = animationStartTime > 0 ? (currentTime - animationStartTime) / 1000f : 0f;

        // If crashed, use crash multiplier for rocket position but continue movement
        float rocketMultiplier = isCrashed ? crashMultiplier : multiplier;

        float targetX = calculateIndependentRocketX(elapsedSeconds);
        float targetY = calculateIndependentRocketY(elapsedSeconds, rocketMultiplier);

        // Smooth interpolation for very small multiplier changes (like 1.01x)
        if (Math.abs(multiplier - currentMultiplier) < 0.05f && !isCrashed) {
            float lerpFactor = 0.3f;
            rocketX = rocketX + (targetX - rocketX) * lerpFactor;
            rocketY = rocketY + (targetY - rocketY) * lerpFactor;
        } else {
            rocketX = targetX;
            rocketY = targetY;
        }

        // Handle edge animation
        float maxRocketX = getWidth() - rocketSize;
        if (rocketX >= maxRocketX && !isCrashed) {
            if (!isAtEdge) {
                isAtEdge = true;
                edgeAnimationStartTime = System.currentTimeMillis();
            }
            rocketX = maxRocketX;
            updateEdgeAnimation();
            rocketY += edgeAnimationOffset;
        } else {
            isAtEdge = false;
        }

        updateIndependentRocketRotation(elapsedSeconds, rocketMultiplier);
        generateIndependentCurvePoint(multiplier);
    }

    /**
     * Calculate independent rocket X position - always moving
     */
    private float calculateIndependentRocketX(float elapsedSeconds) {
        // Rocket continues moving across screen without stopping
        float timeProgress = Math.min(elapsedSeconds / 16f, 1f); // 16 seconds to cross screen
        float targetX = graphStartX + (timeProgress * graphWidth * 0.85f);

        // Handle edge case
        float maxX = getWidth() - rocketSize;
        if (targetX > maxX) {
            targetX = maxX;
        }

        return targetX;
    }

    /**
     * Calculate rocket Y position - always on the red curve line
     */
    private float calculateIndependentRocketY(float elapsedSeconds, float multiplier) {
        // Calculate the exact curve point for current multiplier
        PointF curvePoint = calculatePreciseExponentialCurvePoint(multiplier);

        // Rocket Y position is exactly on the curve
        return curvePoint.y;
    }

    /**
     * Update rocket rotation - always upward at 45 degrees
     */
    private void updateIndependentRocketRotation(float elapsedSeconds, float multiplier) {
        // Always point upward at 45 degrees, connected to red line
        rocketRotation = -40f; // Negative because Y-axis is inverted (up is negative)
    }

    /**
     * Generate curve points - stops at crash, rocket continues
     */
    private void generateIndependentCurvePoint(float multiplier) {
        // If crashed, don't add more curve points (red line stops)
        if (isCrashed) {
            return;
        }

        // Always add current rocket position as the latest curve point
        PointF rocketPoint = new PointF(rocketX, rocketY);

        // Add curve point with precise spacing for smooth curve
        if (graphPoints.isEmpty() ||
                Math.abs(rocketPoint.x - graphPoints.get(graphPoints.size()-1).x) > 1.5f ||
                Math.abs(rocketPoint.y - graphPoints.get(graphPoints.size()-1).y) > 1.5f) {
            graphPoints.add(new PointF(rocketPoint.x, rocketPoint.y));
        }

        // Limit graph points
        if (graphPoints.size() > 1500) {
            graphPoints.remove(0);
        }
    }

    /**
     * Calculate parabolic curve point
     */
    private PointF calculatePreciseExponentialCurvePoint(float multiplier) {
        multiplier = Math.max(1.0f, multiplier);

        long currentTime = System.currentTimeMillis();
        float elapsedSeconds = animationStartTime > 0 ? (currentTime - animationStartTime) / 1000f : 0f;

        float timeProgress = Math.min(elapsedSeconds / 16f, 1f);
        float x = graphStartX + (timeProgress * graphWidth * 0.85f);

        float maxX = getWidth() - rocketSize;
        if (x > maxX) {
            x = maxX;
        }

        // Parabolic curve: y = a * x^2 + b * x + c
        float normalizedX = (x - graphStartX) / (graphWidth * 0.85f); // 0 to 1
        float parabolicY = normalizedX * normalizedX; // x^2 for parabolic shape

        // Scale to screen coordinates - higher curve
        float y = graphStartY - (parabolicY * graphHeight * 1.2f);

        float minY = graphStartY - graphHeight;
        y = Math.max(y, minY);

        return new PointF(x, y);
    }

    /**
     * Update rocket rotation - always upward at 45 degrees
     */
    private void updateRocketRotationFromCurvePrecise(float multiplier) {
        // Always point upward at 45 degrees, connected to red line
        rocketRotation = -40f; // Negative because Y-axis is inverted (up is negative)
    }

    @SuppressLint("SuspiciousIndentation")
    private void startRocketAnimation() {
        if (rocketAnimator != null) {
            rocketAnimator.cancel();
        }

        // Cancel any existing fallback crash timer
        if (crashFallbackRunnable != null) {
            removeCallbacks(crashFallbackRunnable);
        }
        rocketAnimator = ValueAnimator.ofFloat(1.0f, targetMultiplier);
        rocketAnimator.setDuration(animationDuration);
        rocketAnimator.setInterpolator(new LinearInterpolator());

        rocketAnimator.addUpdateListener(animation -> {
            currentMultiplier = (Float) animation.getAnimatedValue();
            displayMultiplier = currentMultiplier;

            updateRocketPositionFromMultiplierPrecise(currentMultiplier);
            updateSlidingWindowsFromMultiplier(currentMultiplier);

            if (animationCallback != null) {
                animationCallback.onAnimationUpdate(currentMultiplier);
            }

            invalidate();
        });

        rocketAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animationCallback != null) {
                    animationCallback.onAnimationComplete(currentMultiplier);
                }
            }
        });

        // Fallback crash timer - ensures rocket always crashes
        crashFallbackRunnable = new Runnable() {
            @Override
            public void run() {
                if (isAnimating && !isCrashed) {
                    crashAnimation();
                }
            }
        };
        postDelayed(crashFallbackRunnable, animationDuration + 500); // 500ms buffer

        rocketAnimator.start();
    }

    public void crashAnimation() {
        // Prevent multiple crashes
        if (isCrashed) {
            return;
        }

        // Cancel fallback crash timer since we're crashing now
        if (crashFallbackRunnable != null) {
            removeCallbacks(crashFallbackRunnable);
            crashFallbackRunnable = null;
        }

        // Mark as crashed and save crash multiplier
        isCrashed = true;
        crashMultiplier = currentMultiplier;

        // Stop main animation but allow rocket to continue moving
        isAnimating = false;
        rocketContinuesMoving = true;
        if (rocketAnimator != null) {
            rocketAnimator.cancel();
        }

        // Start post-crash rocket movement
        startPostCrashRocketMovement();

        // Vibrate phone
        try {
            android.os.Vibrator vibrator = (android.os.Vibrator) getContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(500);
            }
        } catch (Exception e) {
        }

        startScreenShake();

        // Create rocket debris pieces
        for (int i = 0; i < 15; i++) {
            RocketDebris debris = new RocketDebris();
            debris.x = rocketX + random.nextFloat() * 60 - 30;
            debris.y = rocketY + random.nextFloat() * 60 - 30;
            debris.vx = random.nextFloat() * 20 - 10;
            debris.vy = random.nextFloat() * 10 - 5;
            debris.size = random.nextFloat() * 15 + 8;
            debris.rotation = random.nextFloat() * 360;
            debris.rotationSpeed = random.nextFloat() * 10 - 5;
            debris.color = Color.parseColor("#E91E63");
            rocketDebris.add(debris);
        }

        // Create explosion particles
        for (int i = 0; i < 100; i++) {
            Particle particle = new Particle();
            particle.x = rocketX + random.nextFloat() * 80 - 40;
            particle.y = rocketY + random.nextFloat() * 80 - 40;
            particle.vx = random.nextFloat() * 50 - 25;
            particle.vy = random.nextFloat() * 50 - 25;
            particle.size = random.nextFloat() * 25 + 10;
            particle.alpha = 1.0f;
            particle.life = 200;

            if (i % 4 == 0) {
                particle.color = Color.RED;
            } else if (i % 4 == 1) {
                particle.color = Color.YELLOW;
            } else if (i % 4 == 2) {
                particle.color = Color.parseColor("#FF6600");
            } else {
                particle.color = Color.WHITE;
            }

            particles.add(particle);
        }

        graphPaint.setColor(Color.RED);

        // Create explosion clouds (grey smoke)
        for (int i = 0; i < 8; i++) {
            Cloud explosionCloud = new Cloud();
            explosionCloud.x = rocketX + random.nextFloat() * 80 - 40;
            explosionCloud.y = rocketY + random.nextFloat() * 80 - 40;
            explosionCloud.size = random.nextFloat() * 20 + 15; // Smaller clouds
            explosionCloud.alpha = 0.6f;
            explosionCloud.speedX = random.nextFloat() * 2 - 1;
            explosionCloud.isExplosionCloud = true;
            explosionCloud.creationTime = System.currentTimeMillis();
            explosionCloud.fadeSpeed = 0.0016f; // Fade over 5 seconds
            clouds.add(explosionCloud);
        }

        // Create explosion stars
        for (int i = 0; i < 20; i++) {
            Star explosionStar = new Star();
            explosionStar.x = rocketX + random.nextFloat() * 150 - 75;
            explosionStar.y = rocketY + random.nextFloat() * 150 - 75;
            explosionStar.size = random.nextFloat() * 12 + 6;
            explosionStar.alpha = 1.0f;
            explosionStar.twinkleSpeed = -0.0016f; // Fade over 5 seconds
            explosionStar.isExplosionStar = true;
            explosionStar.creationTime = System.currentTimeMillis();
            stars.add(explosionStar);
        }

        // Extended animation
        for (int i = 0; i < 300; i++) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            }, i * 16); // 5 seconds to see rocket move off-screen
        }

        if (animationCallback != null) {
            animationCallback.onAnimationCrash();
        }

    }

    /**
     * Update sliding windows based on current multiplier for better coordination
     */
    private void updateSlidingWindowsFromMultiplier(float multiplier) {
        // Calculate elapsed time for time window sliding
        long currentTime = System.currentTimeMillis();
        if (animationStartTime > 0) {
            float elapsedSeconds = (currentTime - animationStartTime) / 1000f;

            // Update time window based on elapsed time
            if (elapsedSeconds > 4f) {
                targetTimeWindowStart = Math.max(0, (int)(elapsedSeconds - 4)); // Show last 6 seconds
                targetTimeWindowStart = Math.min(targetTimeWindowStart, 12); // Max at 12s
            }
        }

        // Update multiplier window based on current multiplier value
        // Use consistent 0.5x steps for better visibility and smoother scaling
        float multiplierStep = 0.5f;
        int currentStep = (int)((multiplier - 1.0f) / multiplierStep);

        // Show a window of 8 steps centered around current multiplier for better visibility
        int idealStart = Math.max(0, currentStep - 2); // Show 2 steps below current
        targetMultiplierWindowStart = idealStart;

        // Ensure we don't go too high but allow for higher multipliers
        if (targetMultiplierWindowStart > 60) { // Allow up to 30x+ range
            targetMultiplierWindowStart = 60;
        }

    }

    public void resetAnimation() {
        isAnimating = false;
        if (rocketAnimator != null) {
            rocketAnimator.cancel();
        }

        // Cancel fallback crash timer
        if (crashFallbackRunnable != null) {
            removeCallbacks(crashFallbackRunnable);
            crashFallbackRunnable = null;
        }

        // Reset all properties
        isCrashed = false;
        rocketContinuesMoving = false;
        crashMultiplier = 1.0f;
        currentMultiplier = 1.0f;
        displayMultiplier = 1.0f;
        targetMultiplier = 1.0f;
        rocketX = graphStartX;
        rocketY = graphStartY;
        rocketRotation = 0f;
        wingRotation = 0f;
        animationStartTime = 0;
        useBackendMultiplier = false;

        // Reset edge animation
        isAtEdge = false;
        edgeAnimationOffset = 0f;
        edgeAnimationStartTime = 0;

        // Reset cinematic variables
        backgroundScrollOffset = 0f;
        rocketCenterX = 0f;

        // Reset sliding windows and smooth animations
        timeWindowStart = 0;
        multiplierWindowStart = 0;
        timeSlideOffset = 0f;
        multiplierSlideOffset = 0f;
        targetTimeWindowStart = 0;
        targetMultiplierWindowStart = 0;

        // Clear all visual elements
        graphPoints.clear();
        particles.clear();
        trailDots.clear(); // Clear trail dots
        rocketDebris.clear(); // Clear rocket debris

        // Reset graph color
        graphPaint.setColor(Color.RED);

        invalidate();
    }

    private void startPostCrashRocketMovement() {
        // Continue rocket movement for 5 seconds after crash
        postDelayed(new Runnable() {
            @Override
            public void run() {
                rocketContinuesMoving = false;
            }
        }, 5000); // 5 seconds
    }

    // Enhanced Particle class for exhaust trail and explosions
    private static class Particle {
        float x, y;
        float vx, vy;
        float size;
        float alpha;
        int life;
        int color = Color.YELLOW; // Default color
    }

    // Star class for background twinkling stars
    private static class Star {
        float x, y;
        float size;
        float alpha;
        float twinkleSpeed;
        boolean isExplosionStar = false;
        long creationTime = 0;
    }

    // Cloud class for background atmosphere
    private static class Cloud {
        float x, y;
        float size;
        float alpha;
        float speedX;
        boolean isExplosionCloud = false;
        long creationTime = 0;
        float fadeSpeed = 0f;
    }

    // TrailDot class for stardust-like path rendering
    private static class TrailDot {
        float x, y;
        float size;
        float alpha;
        float maxAlpha;
        int color;
        float pulsePhase; // For pulsing effect
        float fadeSpeed;
        long birthTime;
        boolean isGlowing;
        float glowIntensity;

        TrailDot(float x, float y, float size, int color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
            this.maxAlpha = 0.9f;
            this.alpha = maxAlpha;
            this.pulsePhase = (float) (Math.random() * Math.PI * 2);
            this.fadeSpeed = 0.008f + (float) Math.random() * 0.012f; // Varied fade speeds
            this.birthTime = System.currentTimeMillis();
            this.isGlowing = Math.random() < 0.3f; // 30% chance to glow
            this.glowIntensity = 0.5f + (float) Math.random() * 0.5f;
        }

        void update() {
            // Pulsing effect
            pulsePhase += 0.08f;
            float pulse = (float) Math.sin(pulsePhase) * 0.2f + 0.8f;

            // Fade out over time
            alpha -= fadeSpeed;
            alpha = Math.max(0f, alpha);

            // Size variation with pulse
            size = size * pulse;
        }

        boolean isDead() {
            return alpha <= 0f;
        }
    }

    // RocketDebris class for rocket pieces falling down
    private static class RocketDebris {
        float x, y;
        float vx, vy;
        float size;
        float rotation;
        float rotationSpeed;
        int color;
        float alpha = 1.0f;
        int life = 300;

        void update() {
            x += vx;
            y += vy;
            vy += 0.3f; // Gravity
            rotation += rotationSpeed;
            alpha -= 0.003f;
            life--;
        }

        boolean isDead() {
            return alpha <= 0f || life <= 0;
        }
    }
}
