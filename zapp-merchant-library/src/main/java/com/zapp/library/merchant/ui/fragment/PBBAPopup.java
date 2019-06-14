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
package com.zapp.library.merchant.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.zapp.library.merchant.R;
import com.zapp.library.merchant.network.AvailableBankAppsLoader;
import com.zapp.library.merchant.network.response.AvailableBankAppsResponse;
import com.zapp.library.merchant.network.response.PBBABankLogoResponse;
import com.zapp.library.merchant.ui.PBBAPopupCallback;
import com.zapp.library.merchant.ui.view.CustomButtonView;
import com.zapp.library.merchant.util.FilterAvailableBankAppsByLargeLogo;
import com.zapp.library.merchant.util.RichStyleStringBuilder;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The base Pay by Bank app Popup Fragment.
 *
 * @author msagi
 * @since 1.0.0
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class PBBAPopup extends DialogFragment implements LoaderManager.LoaderCallbacks<PBBABankLogoResponse> {

    private static final int DISPLAY_COUNT = 4;
    private static final int AVAILABLE_BANK_APPS_LOADER_ID = 3;

    /**
     * The gateway response for the "available bank apps"
     */
    private PBBABankLogoResponse mGetAvailableBankAppsResponse;

    private LinearLayout ltAvailableAppsFirstBlock;
    private LinearLayout ltAvailableAppsSecondBlock;
    private View ltAvailableAppsBlock;

    /**
     * Tag for logging and fragment tagging.
     */
    @SuppressWarnings("ConstantNamingConvention")
    public static final String TAG = PBBAPopup.class.getSimpleName();

    /**
     * The (weak reference to the) callback interface to the popup controller.
     */
    @Nullable
    private WeakReference<PBBAPopupCallback> mCallbackWeakReference;
    protected TextView secureMessageText;
    protected RelativeLayout ltThirdStepLayout;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(STYLE_NORMAL, R.style.PBBAPopup_Screen);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        //auto re-connect if callback is not set and attached to a suitable context
        //noinspection InstanceofIncompatibleInterface
        if (context instanceof PBBAPopupCallback && getCallback() == null) {
            //noinspection CastToIncompatibleInterface
            setCallback((PBBAPopupCallback) context);
        }
    }

    /**
     * Set the callback listener.
     *
     * @param callback The callback listener instance.
     */
    public void setCallback(@NonNull final PBBAPopupCallback callback) {
        mCallbackWeakReference = new WeakReference<>(callback);
    }

    /**
     * Close the popup and fire dismiss event on the callback interface.
     */
    public void dismissWithCallback() {
        final PBBAPopupCallback callback = getCallback();
        if (callback != null) {
            callback.onDismissPopup();
        }
        dismiss();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageView closePopupButton = (ImageView) view.findViewById(R.id.pbba_popup_close);
        if (closePopupButton != null) {
            closePopupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    dismissWithCallback();
                }
            });
        }

        final AppCompatImageView image = (AppCompatImageView) view.findViewById(R.id.pbba_popup_pay_by_bank_app_logo_text);
        if (image != null) {
           final AccessibilityDelegateCompat delegate = new AccessibilityDelegateCompat() {
                @Override
                public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                    info.setImportantForAccessibility(true);
                    info.setFocusable(true);
                    super.onInitializeAccessibilityNodeInfo(host, info);
                    info.setContentDescription(getString(R.string.pbba_button_content_description));
                }
            };

            final Runnable task = new Runnable() {
                @Override
                public void run() {
                    image.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                }
            };
            final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
            worker.schedule(task, 500, TimeUnit.MILLISECONDS);

            ViewCompat.setAccessibilityDelegate(image, delegate);
        }

        secureMessageText = (TextView) view.findViewById(R.id.pbba_popup_ecomm_secure_message);
        ltThirdStepLayout = (RelativeLayout) view.findViewById(R.id.pbba_popup_third_step_layout);

        ltAvailableAppsFirstBlock = (LinearLayout) view.findViewById(R.id.pbba_popup_about_available_apps_first);
        ltAvailableAppsSecondBlock = (LinearLayout) view.findViewById(R.id.pbba_popup_about_available_apps_second);
        ltAvailableAppsBlock = view.findViewById(R.id.pbba_popup_about_available_apps_block);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.v(TAG, String.format("onActivityCreated (%s): savedInstanceState: %s, activity: %s", this, savedInstanceState, getActivity()));
        super.onActivityCreated(savedInstanceState);
        if (shouldLoadAvailableBankApps()) {
            Log.v(TAG, "onActivityCreated initLoader: ");
            getLoaderManager().initLoader(AVAILABLE_BANK_APPS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<PBBABankLogoResponse> onCreateLoader(final int id, final Bundle args) {
        return new AvailableBankAppsLoader(getActivity(), new FilterAvailableBankAppsByLargeLogo());
    }

    @Override
    public void onLoadFinished(final Loader<PBBABankLogoResponse> loader, final PBBABankLogoResponse data) {
        mGetAvailableBankAppsResponse = data;
        if (ltAvailableAppsBlock != null && ltAvailableAppsFirstBlock != null && ltAvailableAppsSecondBlock != null) {
            updateUI();
        }
    }

    private void updateUI() {
        final List<AvailableBankAppsResponse> list = mGetAvailableBankAppsResponse.getAvailableBankAppsResponseList();
        if (list != null && list.size() > 0) {
            if (list.size() > DISPLAY_COUNT) {
                final List<AvailableBankAppsResponse> firstBlock = list.subList(0, DISPLAY_COUNT);
                final List<AvailableBankAppsResponse> secondBlock = list.subList(DISPLAY_COUNT, list.size());
                displayLogo(ltAvailableAppsFirstBlock, firstBlock);
                displayLogo(ltAvailableAppsSecondBlock, secondBlock);
            } else {
                displayLogo(ltAvailableAppsFirstBlock, list);
            }
            ltAvailableAppsBlock.setVisibility(View.VISIBLE);
        } else {
            ltAvailableAppsBlock.setVisibility(View.GONE);
        }
    }

    private void displayLogo(final LinearLayout view, final List<AvailableBankAppsResponse> list) {
        view.setVisibility(View.VISIBLE);
        for (final AvailableBankAppsResponse bank : list) {
            final View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.pbba_popup_about_item_app,
                    null);
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.pbba_available_bank_image);
            imageView.setContentDescription(bank.getBankName() + getString(R.string.accesibility_image_text));
            view.addView(rootView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            Glide.with(this).load(bank.getLargeLogo())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(getContext().getResources().getDimensionPixelSize(R.dimen.pbba_available_bank_app_corner))))
                    .apply(RequestOptions.centerCropTransform())
                    .into(imageView);
        }
    }

    protected void configureStepsTextMessage(final View view) {
        final int orangeTextColor = ContextCompat.getColor(getContext(), R.color.pbba_step_message_orange_color);
        final Typeface medium = Typeface.createFromAsset(getContext().getAssets(), getString(R.string.pbba_popup_semibold_font));

        final TextView firstStepMessageView = (TextView) view.findViewById(R.id.pbba_ecomm_first_step_text_view);
        final String firstStepLogIn = getString(R.string.pbba_ecomm_first_step_log_in);
        final String firstStepFullText = getString(R.string.pbba_ecomm_first_step_text, firstStepLogIn);
        final RichStyleStringBuilder firstStep = RichStyleStringBuilder.of(firstStepFullText);
        firstStep.withColor(firstStepLogIn, orangeTextColor);
        firstStep.withFont(firstStepLogIn, medium);
        firstStepMessageView.setText(firstStep.build());

        final TextView secondStepMessageView = (TextView) view.findViewById(R.id.pbba_ecomm_second_step_text_view);
        final String secondStepFind = getString(R.string.pbba_ecomm_second_step_find);
        final String secondPBBAText = getString(R.string.pbba_ecomm_second_step_pay_by_bank);
        final String secondFullText = getString(R.string.pbba_ecomm_second_step_text, secondStepFind, secondPBBAText);
        final RichStyleStringBuilder secondStep = RichStyleStringBuilder.of(secondFullText);
        secondStep.withColor(secondStepFind, orangeTextColor);
        secondStep.withFont(secondStepFind, medium);
        secondStep.withFont(secondPBBAText, medium);
        secondStepMessageView.setText(secondStep.build());

        final String thirdStepEnter = getString(R.string.pbba_ecomm_third_step_enter);
        final String thirdFullText = getString(R.string.pbba_ecomm_third_step_text, thirdStepEnter);
        final RichStyleStringBuilder thirdStep = RichStyleStringBuilder.of(thirdFullText);
        thirdStep.withColor(thirdStepEnter, orangeTextColor);
        thirdStep.withFont(thirdStepEnter, medium);
        secureMessageText.setText(thirdStep.build());
    }

    @Override
    public void onLoaderReset(final Loader<PBBABankLogoResponse> loader) {
        Log.v(TAG, String.format("onLoaderReset (%s): loader: %s", this, loader));
    }

    /**
     * Get the Pay by Bank app popup callback handler.
     *
     * @return The {@link PBBAPopupCallback callback}.
     */
    @Nullable
    PBBAPopupCallback getCallback() {
        return mCallbackWeakReference != null ? mCallbackWeakReference.get() : null;
    }

    /**
     * Should be override in case when on popup shouldn't load available bank apps
     *
     * @return boolean value. If it's true will load the available bank apps otherwise won't load
     */
    protected boolean shouldLoadAvailableBankApps() {
        return true;
    }

    /**
     * Display error layout with message, title and try again button
     *
     * @param errorTitle   The error title to use
     * @param errorMessage The error message to use.
     */
    public void setErrorMessage(@NonNull final String errorTitle, @NonNull final String errorMessage) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.pbba_popup_error_layout, null, false);
        final TextView title = (TextView) view.findViewById(R.id.pbba_popup_error_title_text);
        final TextView message = (TextView) view.findViewById(R.id.pbba_popup_error_message_text);
        final CustomButtonView tryAgain = (CustomButtonView) view.findViewById(R.id.pbba_popup_error_button);

        if (tryAgain != null) {
            tryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PBBAPopupCallback callback = getCallback();
                    if (callback != null) {
                        callback.onRetryPaymentRequest();
                    }
                }
            });
        }
        title.setText(errorTitle);
        message.setText(errorMessage);

        if (ltThirdStepLayout != null) {
            ltThirdStepLayout.removeAllViews();
            ltThirdStepLayout.addView(view);
            if (ltThirdStepLayout != null) {
                ltThirdStepLayout.removeAllViews();
                ltThirdStepLayout.addView(view);

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        title.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                    }
                };
                final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
                worker.schedule(task, 1, TimeUnit.SECONDS);
            }
        }
        if (secureMessageText != null) {
            secureMessageText.setVisibility(View.GONE);
        }
    }

}
