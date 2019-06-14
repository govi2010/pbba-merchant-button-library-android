package com.zapp.library.merchant.util;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * Custom typeFaceSpan
 */
public final class CustomTypeFaceSpan extends TypefaceSpan {
    private final Typeface newType;

    CustomTypeFaceSpan(@NonNull final Typeface type) {
        this("", type);
    }

    private CustomTypeFaceSpan(@NonNull final String family, @NonNull final Typeface type) {
        super(family);
        newType = type;
    }

    @Override
    public void updateDrawState(@NonNull final TextPaint textPaint) {
        applyCustomTypeFace(textPaint, newType);
    }

    @Override
    public void updateMeasureState(@NonNull final TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }

    private static void applyCustomTypeFace(@NonNull final Paint paint, @NonNull final Typeface tf) {
        final int oldStyle;
        final Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        final int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }
}
