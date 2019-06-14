package com.zapp.library.merchant.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.zapp.library.merchant.R;

public class CustomButtonView extends Button {

    /**
     * Create new instance.
     *
     * @param context The context to use.
    public CustomButtonView(final Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Create new instance.
     *
     * @param context The context to use.
     * @param attrs   The attribute set to use.
     */
    public CustomButtonView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Create new instance.
     *
     * @param context  The context to use.
     * @param attrs    The attribute set to use.
     * @param defStyle The default type id to use.
     */
    public CustomButtonView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Initialize custom view.
     *
     * @param context The context to use.
     * @param attrs   The attribute set to use.
     */
    @TargetApi(24)
    private void init(final Context context, final AttributeSet attrs) {
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0);

        try {
            final String fontName = a.getString(R.styleable.CustomTextView_fontName);
            final Typeface customFont = Typeface.createFromAsset(context.getAssets(), fontName);
            setTypeface(customFont);
        } finally {
            a.recycle();
        }
    }

}
