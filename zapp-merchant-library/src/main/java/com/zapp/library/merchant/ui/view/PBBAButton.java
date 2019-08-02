/*
 * Copyright 2016 IPCO 2012 Limited
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.zapp.library.merchant.ui.view;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.zapp.library.merchant.R;
import com.zapp.library.merchant.network.AvailableBankAppsLoader;
import com.zapp.library.merchant.network.response.AvailableBankAppsResponse;
import com.zapp.library.merchant.network.response.PBBABankLogoResponse;
import com.zapp.library.merchant.util.FilterAvailableBankAppsBySmallLogo;
import com.zapp.library.merchant.util.PBBAAppUtils;
import com.zapp.library.merchant.util.PBBALibraryUtils;

import java.util.List;

/**
 * The Pay by Bank app button to include in the Merchant App. This is a plain compound view without any custom logic and can be used as a simple button.
 * <p>
 * To display it in a layout, insert the following snippet:<br>
 * <p>
 * {@code
 * <com.zapp.library.merchant.ui.view.PBBAButton
 * android:id="@+id/pbba_button"
 * android:layout_width="@dimen/pbba_button_width"
 * android:layout_height="@dimen/pbba_button_height" />
 * }
 * <p>
 * The button theme can be configured as per the Pay by Bank app Developer Guide documentation.
 *
 * @author msagi
 * @since 1.0.0
 */
public class PBBAButton extends RelativeLayout implements LoaderManager.LoaderCallbacks<PBBABankLogoResponse>, View.OnClickListener {
    private static final int AVAILABLE_BANK_APPS_LOADER_ID = 3;
    private static final String TAG = PBBAButton.class.getSimpleName();
    /**
     * Max count of bank logos which can be display on button
     */
    private static final int BANK_ICONS_MAX_DISPLAY_COUNT = 8;
    /**
     * When Count is 1 or less , buttonlogo and Text Images are center aligned
     */
    private static final int BANK_ICONS_DISPLAY_COUNT_TYPE1 = 1;
    /**
     * When Count is 4 or greater than 1 , buttonlogo and Text Images are left aligned
     */
    private static final int BANK_ICONS_DISPLAY_COUNT_TYPE2 = 4;
    /**
     * When Count is 6 or greater than 4 , buttonlogo and Text Images are left aligned, Text image 2 liner
     */
    private static final int BANK_ICONS_DISPLAY_COUNT_TYPE3 = 6;
    /**
     * When Count is 8 or greater than 6, buttonlogo is left aligned and text image removed from view
     */
    private static final int BANK_ICONS_DISPLAY_COUNT_TYPE4 = 8;

    /**
     * click listner tags for Button and more about PbBa view
     */
    private static final int mButtonContainerTAG = 1;
    private static final int mMoreAboutPbBaContainerTAG = 2;

    private PBBABankLogoResponse mGetAvailableBankApssResponse;

    /**
     * The Linear Layout for displaying bank app logos on the button.
     */
    private LinearLayout llBankAppLogo;
    private AppCompatImageView pbbaBankAppText;
    private AppCompatImageView pbbaBankAppProgressIcon;
    /**
     * The button view.
     */
    private final View mMoreAboutButtonContainer;

    /**
     * Bundle key for super instance state field.
     */
    private static final String KEY_SUPER_INSTANCE_STATE = "key.super.instanceState";

    /**
     * Bundle key for 'is enabled' button state field.
     */
    private static final String KEY_IS_ENABLED = "key.isEnabled";

    /**
     * Timeout for auto re-enabling button.
     */
    private static final long AUTO_RE_ENABLE_TIMEOUT = 10000L;

    /**
     * The progress animation for the button.
     */
    private ImageView mAnimatedIcon;

    /**
     * The button view.
     */
    private final View mButtonContainer;

    /**
     * The buttons click listener.
     */
    private OnClickListener mClickListener;

    /**
     * The Pay by Bank app icon on the button.
     */
    private Drawable mIcon;

    /**
     * The Pay by Bank app icon animation on the button.
     */
    private Drawable mIconAnimation;

    /**
     * Worker for button auto re-enable feature.
     */
    private final Runnable mAutoReEnableWorker = new Runnable() {
        @Override
        public void run() {
            setEnabled(true);
        }
    };

    /**
     * Creates a new Pay by Bank app button.
     *
     * @param context The view context.
     */
    public PBBAButton(final Context context) {
        this(context, null);
    }

    /**
     * Creates a new Pay by Bank app button.
     *
     * @param context The view context.
     * @param attrs   The styled attributes.
     */
    @SuppressWarnings("ThisEscapedInObjectConstruction")
    public PBBAButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        View view;

        int pbbaTheme = PBBALibraryUtils.getPbbaTheme(context);

        if (pbbaTheme == PBBALibraryUtils.PBBA_THEME_STANDARD) {
            view = inflate(getContext(), R.layout.pbba_button_pay_by_bank_app, this);
            mButtonContainer = view.findViewById(R.id.pbba_button_container);
            mButtonContainer.setTag(mButtonContainerTAG);

            mMoreAboutButtonContainer = view.findViewById(R.id.pbba_button_more_about_container);
            mMoreAboutButtonContainer.setTag(mMoreAboutPbBaContainerTAG);

            mAnimatedIcon = (ImageView) view.findViewById(R.id.progress);
            mIconAnimation = getDrawable(R.drawable.pbba_animation);
            mIcon = getDrawable(R.drawable.pbba_symbol_icon);
            mAnimatedIcon.setImageDrawable(mIcon);

            llBankAppLogo = (LinearLayout) view.findViewById(R.id.ll_bankapplogos);
            pbbaBankAppText = (AppCompatImageView) view.findViewById(R.id.pbba_button_pay_by_bank_app_text);
            pbbaBankAppProgressIcon = (AppCompatImageView) view.findViewById(R.id.progress);
        } else if (pbbaTheme == PBBALibraryUtils.PBBA_THEME_PINGIT_LIGHT) {
            view = inflate(getContext(), R.layout.pbba_button_cobranded, this);
            mButtonContainer = view.findViewById(R.id.pbba_button_container);
            mMoreAboutButtonContainer = view.findViewById(R.id.pbba_button_more_about_container_linetwo);
            mButtonContainer.setBackgroundResource(R.drawable.pingit_light_button_selector);
            final ImageView brandImageView = (ImageView) view.findViewById(R.id.pbba_brand_image);
            brandImageView.setImageDrawable(getDrawable(R.drawable.pingit_light_button_image));
        } else if (pbbaTheme == PBBALibraryUtils.PBBA_THEME_PINGIT_DARK) {
            view = inflate(getContext(), R.layout.pbba_button_cobranded, this);
            mButtonContainer = view.findViewById(R.id.pbba_button_container);
            mMoreAboutButtonContainer = view.findViewById(R.id.pbba_button_more_about_container_linetwo);
            mButtonContainer.setBackgroundResource(R.drawable.pingit_dark_button_selector);
            final ImageView brandImageView = (ImageView) view.findViewById(R.id.pbba_brand_image);
            brandImageView.setImageDrawable(getDrawable(R.drawable.pingit_dark_button_image));
        } else {
            throw new IllegalStateException("pbbaTheme value not supported");
        }

        mButtonContainer.setOnClickListener(this);
        mMoreAboutButtonContainer.setOnClickListener(this);
    }

    /**
     * Initializing Loader Manager while loading the PBBA Button
     *
     * @param loaderManager
     */
    public void initLoaderManager(@NonNull final LoaderManager loaderManager) {

        loaderManager.restartLoader(AVAILABLE_BANK_APPS_LOADER_ID, null, this);

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle state = new Bundle();
        state.putParcelable(KEY_SUPER_INSTANCE_STATE, super.onSaveInstanceState());
        state.putBoolean(KEY_IS_ENABLED, isEnabled());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        final Bundle savedInstanceState = (Bundle) state;
        final Parcelable superInstanceState = savedInstanceState.getParcelable(KEY_SUPER_INSTANCE_STATE);
        final Boolean isEnabled = savedInstanceState.getBoolean(KEY_IS_ENABLED, Boolean.TRUE);
        if (!isEnabled) {
            setEnabled(false);
        }
        super.onRestoreInstanceState(superInstanceState);
    }

    @Override
    public void onClick(final View v) {
        if (v.getTag().equals(mButtonContainerTAG)) {
            setEnabled(false);
            if (mClickListener != null) {
                mClickListener.onClick(this);
            }
        } else if (v.getTag().equals(mMoreAboutPbBaContainerTAG)) {
            PBBAAppUtils.showPBBAPopupAbout(((FragmentActivity) getContext()));
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        removeCallbacks(mAutoReEnableWorker);
        super.setEnabled(enabled);
        if (enabled) {
            stopAnimation();
        } else {
            startAnimation();
            postDelayed(mAutoReEnableWorker, AUTO_RE_ENABLE_TIMEOUT);
        }
        mButtonContainer.setEnabled(enabled);
    }

    @Override
    public void setOnClickListener(final OnClickListener listener) {
        super.setOnClickListener(listener);
        this.mClickListener = listener;
    }

    /**
     * Start the Pay by Bank app button icon animation.
     */
    private void startAnimation() {
        if (mAnimatedIcon != null) {
            mAnimatedIcon.setImageDrawable(mIconAnimation);
            // Start the animation (looped playback by default).
            ((Animatable) mIconAnimation).start();
        }
    }

    /**
     * Stop the Pay by Bank app button icon animation.
     */
    private void stopAnimation() {
        if (mAnimatedIcon != null) {
            ((Animatable) mIconAnimation).stop();
            mAnimatedIcon.setImageDrawable(mIcon);
        }
    }

    /**
     * Get drawable from resource id.
     *
     * @param drawableResId The resource id of the drawable.
     * @return The drawable instance.
     */
    @SuppressWarnings("deprecation")
    private Drawable getDrawable(@DrawableRes final int drawableResId) {
        final Resources res = getResources();

        final Drawable drawable;
        //noinspection IfMayBeConditional
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = res.getDrawable(drawableResId, null);
        } else {
            drawable = res.getDrawable(drawableResId);
        }
        return drawable;
    }

    @Override
    public Loader<PBBABankLogoResponse> onCreateLoader(final int id, final Bundle args) {
        return new AvailableBankAppsLoader(getContext(), new FilterAvailableBankAppsBySmallLogo());
    }

    @Override
    public void onLoadFinished(final Loader<PBBABankLogoResponse> loader, final PBBABankLogoResponse data) {
        Log.v(TAG, "onLoadFinished");
        mGetAvailableBankApssResponse = data;
        updatePbBaButtonUI();
    }

    @Override
    public void onLoaderReset(final Loader<PBBABankLogoResponse> loader) {
        Log.e(TAG, String.format("onLoaderReset (%s): loader: %s", this, loader));
    }

    /**
     * Fetch the bank images and update PbBa Button Layout
     */
    private void updatePbBaButtonUI() {
        if (mGetAvailableBankApssResponse.getAvailableBankAppsResponseList() != null && mGetAvailableBankApssResponse.getAvailableBankAppsResponseList().size() > 0) {
            final List<AvailableBankAppsResponse> list = mGetAvailableBankApssResponse.getAvailableBankAppsResponseList();
            if (list.size() > BANK_ICONS_MAX_DISPLAY_COUNT) {
                setPbBaButtonLayout(list.subList(0, BANK_ICONS_MAX_DISPLAY_COUNT));
            } else {
                setPbBaButtonLayout(list);
            }
        }
    }

    /**
     * Set PbBa button layout views according to bank app list fectch
     *
     * @param list
     */
    private void setPbBaButtonLayout(final List<AvailableBankAppsResponse> list) {
        if (list.size() > 0) {
            for (AvailableBankAppsResponse bank : list) {
                final ImageView imageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.pbba_button_item_bankapp_icon,
                        null);
                final int pbbaButtonBankIconSize = getResources().getDimensionPixelSize(R.dimen.pbba_button_bank_icon_size);
                final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pbbaButtonBankIconSize, pbbaButtonBankIconSize);
                layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.pbba_button_bank_icon_margin), 0, getResources().getDimensionPixelSize(R.dimen.pbba_button_bank_icon_margin), 0);
                layoutParams.gravity = Gravity.END;
                imageView.setMinimumWidth(pbbaButtonBankIconSize);
                imageView.setMaxWidth(pbbaButtonBankIconSize);
                llBankAppLogo.addView(imageView, layoutParams);
                Glide.with(getContext())
                        .load(bank.getSmallLogo())
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(getContext().getResources().getDimensionPixelSize(R.dimen.pbba_available_bank_app_corner_pbba_button))))
                        .into(imageView);
            }

            //type1 = if number of bank icons are 0 or 1
            //type2 = if number of bank icons are 2 or 3 or 4
            //type3 = if number of bank icons are 5 or 6
            //type4 = if number of bank icons are 7 or 8
            if (list.size() <= BANK_ICONS_DISPLAY_COUNT_TYPE1) {
//                 Bank app icon images Linear layout margin
                llBankAppLogo.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                llBankAppLogo.setGravity(Gravity.END | Gravity.RIGHT);
                ((LinearLayout) mButtonContainer).setGravity(Gravity.RIGHT | Gravity.CENTER);
                ((LinearLayout) mButtonContainer).setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.pbba_button_container_padding_start), 0);
                final MarginLayoutParams layoutParams = (MarginLayoutParams) llBankAppLogo.getLayoutParams();
                final int llBankAppLogo_marginLeft = getResources().getDimensionPixelSize(R.dimen.pbba_button_type1_bankappicon_margin_left);
                final int llBankAppLogo_marginRight = getResources().getDimensionPixelSize(R.dimen.pbba_button_type1_bankappicon_margin_right);
                layoutParams.setMargins(llBankAppLogo_marginLeft, 0, llBankAppLogo_marginRight, 0);
//                pay by bank app text margin
//                final MarginLayoutParams pbbaBankAppTextLayoutParams = (MarginLayoutParams)pbbaBankAppText.getLayoutParams();
//                pbbaBankAppTextLayoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.pbba_button_type1_bankapptext_margin_left),0,0,0);
            } else if (list.size() > BANK_ICONS_DISPLAY_COUNT_TYPE1 && list.size() <= BANK_ICONS_DISPLAY_COUNT_TYPE2) {
//                main pbba button container gravity and padding change for type2
                ((LinearLayout) mButtonContainer).setGravity(Gravity.LEFT | Gravity.CENTER);
                ((LinearLayout) mButtonContainer).setPadding(getResources().getDimensionPixelSize(R.dimen.pbba_button_container_padding_start), 0, 0, 0);
//                pay by bank app text margin
                final MarginLayoutParams layoutParams = (MarginLayoutParams) pbbaBankAppText.getLayoutParams();
                layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.pbba_button_type2_bankapptext_margin_left), 0, 0, 0);
            } else if (list.size() > BANK_ICONS_DISPLAY_COUNT_TYPE2 && list.size() <= BANK_ICONS_DISPLAY_COUNT_TYPE3) {
//                main pbba button container gravity and padding change for type3
                ((LinearLayout) mButtonContainer).setGravity(Gravity.LEFT | Gravity.CENTER);
                ((LinearLayout) mButtonContainer).setPadding(getResources().getDimensionPixelSize(R.dimen.pbba_button_container_padding_start), 0, 0, 0);
//                pbba button text margin for type3
                final MarginLayoutParams layoutParams = (MarginLayoutParams) pbbaBankAppText.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);
//                setting up 2 liner text for pbba button for type3
                pbbaBankAppText.setImageResource(R.drawable.pbba_button_text_twoline);
//                added padding for pbba button text
                final int leftPadding = (getResources().getDimensionPixelSize(R.dimen.pbba_button_type3_bankapptext_padding_left)) * (-1);
                pbbaBankAppText.setPadding(leftPadding, getResources().getDimensionPixelSize(R.dimen.pbba_button_type3_bankapptext_padding_top), 0, getResources().getDimensionPixelSize(R.dimen.pbba_button_type3_bankapptext_padding_bottom));
            } else if (list.size() > BANK_ICONS_DISPLAY_COUNT_TYPE3 && list.size() <= BANK_ICONS_DISPLAY_COUNT_TYPE4) {
//                main pbba button container gravity and padding change for type3
                ((LinearLayout) mButtonContainer).setGravity(Gravity.LEFT | Gravity.CENTER);
                ((LinearLayout) mButtonContainer).setPadding(getResources().getDimensionPixelSize(R.dimen.pbba_button_container_padding_start), 0, 0, 0);
//                changing visibility for pbba button text for type 4
                pbbaBankAppText.setVisibility(View.GONE);
            }
            llBankAppLogo.setVisibility(View.VISIBLE);
        } else {
            ((LinearLayout) mButtonContainer).setGravity(Gravity.CENTER);
        }
    }
}
