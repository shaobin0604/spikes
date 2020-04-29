package com.mobodev.spikes.waveprogresstextview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * based on Titanic by @romainpiel 13/03/2014
 */
public class WaveProgressTextView extends AppCompatTextView {
    private static final String TAG = "ProgressWaveTextView";

    private static final int PROGRESS_MAX_DEFAULT = 100;

    private ObjectAnimator maskXAnimator;
    // wave shader coordinates
    private float maskX, maskY;
    // if true, the shader will display the wave
    private boolean sinking;
    // true after the first onSizeChanged
    private boolean setUp;
    // shader containing a repeated wave
    private BitmapShader shader;
    // shader matrix
    private Matrix shaderMatrix;
    // wave drawable
    private Drawable wave;
    // (getTextSize() - waveHeight) / 2
    private float offsetY;
    private int waveW;
    private int waveH;
    // 0..100
    private int progress;
    private int progressMax = PROGRESS_MAX_DEFAULT;
    private int waveColor;

    public WaveProgressTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public WaveProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public WaveProgressTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaveProgressTextView, defStyle, 0);
            try {
                wave = a.getDrawable(R.styleable.WaveProgressTextView_waveDrawable);
                waveColor = a.getColor(R.styleable.WaveProgressTextView_waveColor, Color.BLUE);
            } finally {
                a.recycle();
            }
        }

        shaderMatrix = new Matrix();

        if (wave == null) {
            wave = getResources().getDrawable(R.drawable.wave_default);
        }
        wave.setTint(waveColor);

        waveW = wave.getIntrinsicWidth();
        waveH = wave.getIntrinsicHeight();

        maskXAnimator = ObjectAnimator.ofFloat(this, "maskX", 0, waveW);
        maskXAnimator.setInterpolator(new LinearInterpolator());
        maskXAnimator.setRepeatCount(ValueAnimator.INFINITE);
        maskXAnimator.setDuration(1000);
        maskXAnimator.setStartDelay(0);
        maskXAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                Log.v(TAG, "onAnimationCancel");
                forceRedraw();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.v(TAG, "onAnimationEnd");
                forceRedraw();
            }

            private void forceRedraw() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    postInvalidate();
                } else {
                    postInvalidateOnAnimation();
                }
            }
        });
    }

    public float getMaskX() {
        return maskX;
    }

    public void setMaskX(float maskX) {
        this.maskX = maskX;
        invalidate();
    }

    public float getMaskY() {
        return maskY;
    }

    public void setMaskY(float maskY) {
        this.maskY = maskY;
        invalidate();
    }

    public boolean isSinking() {
        return sinking;
    }

    public void setSinking(boolean sinking) {
        this.sinking = sinking;

        if (setUp) {
            if (sinking) {
                maskXAnimator.start();
            } else {
                maskXAnimator.cancel();
            }
        }
    }

    public int getProgress() {
        return progress;
    }

    /**
     * @param progress 0..100
     */
    public void setProgress(int progress) {
        // maskY = 0 -> wave vertically centered
        this.progress = Math.max(Math.min(progress, progressMax), 0);
        Log.v(TAG, String.format("setProgress, param progress: %d, field progress: %d", progress, this.progress));
        if (setUp) {
            final float textSize = getTextSize();
            maskY = (50 - this.progress) * textSize / 100;
            invalidate();
        }
    }

    public boolean isSetUp() {
        return setUp;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.v(TAG, "onSizeChanged");
        createShader();

        if (!setUp) {
            setUp = true;
            if (sinking) {
                maskXAnimator.start();
            } else {
                maskXAnimator.cancel();
            }

            // 这里用 TextSize 而不是 View Height 是因为 TextView 的高度通常会大于字号
            final float textSize = getTextSize();
            maskY = (50 - this.progress) * textSize / 100;
            invalidate();
        }
    }

    /**
     * Create the shader
     * draw the wave with current color for a background
     * repeat the bitmap horizontally, and clamp colors vertically
     */
    private void createShader() {
        Bitmap b = Bitmap.createBitmap(waveW, waveH, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        c.drawColor(getCurrentTextColor());

        wave.setBounds(0, 0, waveW, waveH);
        wave.draw(c);

        shader = new BitmapShader(b, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        getPaint().setShader(shader);

        offsetY = (getTextSize() - waveH) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // modify text paint shader according to sinking state
        if (sinking && shader != null) {

            // first call after sinking, assign it to our paint
            if (getPaint().getShader() == null) {
                getPaint().setShader(shader);
            }

            // translate shader accordingly to maskX maskY positions
            // maskY is affected by the offset to vertically center the wave
            shaderMatrix.setTranslate(maskX, maskY + offsetY);

            // assign matrix to invalidate the shader
            shader.setLocalMatrix(shaderMatrix);
        } else {
            getPaint().setShader(null);
        }

        super.onDraw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        maskXAnimator.cancel();
    }
}
