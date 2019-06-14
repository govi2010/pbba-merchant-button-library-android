package com.zapp.library.merchant.util;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

/**
 * Helper class to create spannable string with possibility to set custom color and font for specific string
 */
public final class RichStyleStringBuilder {
    private final SpannableString content;
    private final String fullText;

    private RichStyleStringBuilder(@NonNull final String fullText) {
        this.fullText = fullText;
        this.content = new SpannableString(fullText);
    }

    /**
     *  Create instance of class
     *
     * @param text string text
     * @return instance of class
     */
    public static RichStyleStringBuilder of(@NonNull final String text) {
        return new RichStyleStringBuilder(text);
    }

    /**
     * Set specific color for specific string from whole string
     *
     * @param text string text that will set the custom color
     * @param color value of color
     * @return instance
     */
    public RichStyleStringBuilder withColor(@NonNull final String text, final int color) {
        if (TextUtils.isEmpty(text) || color == 0) {
            return this;
        }

        try {
            final int start = fullText.indexOf(text);
            final int end = start + text.length();

            content.setSpan(new ForegroundColorSpan(color), start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            return this;
        }
        return this;
    }

    /**
     * Set specific font for specific string from whole string
     *
     * @param text string text that will set the custom font
     * @param font TypeFace that will set for specific string
     * @return instance
     */
    public RichStyleStringBuilder withFont(@NonNull final String text, @NonNull final Typeface font) {
        if (TextUtils.isEmpty(text)) {
            return this;
        }

        try {
            final int start = fullText.indexOf(text);
            final int end = start + text.length();

            content.setSpan(new CustomTypeFaceSpan(font), start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            return this;
        }
        return this;
    }

    /**
     * Return the spannableString with custom color for specific string or/and custom font for specific string
     *
     * @return spannableString
     */
    public SpannableString build() {
        return content;
    }
}
