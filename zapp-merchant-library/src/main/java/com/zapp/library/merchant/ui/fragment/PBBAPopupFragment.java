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

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import com.zapp.library.merchant.R;
import com.zapp.library.merchant.ui.PBBAPopupCallback;
import com.zapp.library.merchant.ui.view.CustomTextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Popup fragment for Zapp electronic and mobile commerce (e-Comm and m-Comm) journeys.
 *
 * @author msagi
 * @since 1.0.0
 */
@SuppressWarnings("CyclicClassDependency")
public final class PBBAPopupFragment extends PBBAPopup {

    private static final int CRITICAL_TIME_VALUE = 30;
    /**
     * Key for fragment argument: BRN.
     */
    private static final String KEY_BRN = "key.brn";
    /**
     * Key for fragment argument: Secure Token.
     */
    private static final String KEY_SECURE_TOKEN = "key.secureToken";
    /**
     * Key for fragment argument: Timeout
     */
    private static final String KEY_TIMEOUT = "key.timeoutTS";

    /**
     * Key for time value
     */
    private static final String KEY_TIME_VALUE = "key.timeValue";
    /**
     * No timer value in saveInstanceState
     */
    private static final int NO_TIMER_VALUE = -1;
    /**
     * Expired value
     */
    private static final int EXPIRED_VALUE = 0;
    /**
     * The BRN code for this popup.
     */
    private String mBrn;

    /**
     * The secure token for this popup.
     */
    private String mSecureToken;

    /**
     * The timeout for checking transaction status
     */
    private long mTimeoutTS;

    private CountDownTimer countDown;
    private TextView countDownTimer;

    /**
     * Create new instance.
     *
     * @param secureToken The secure token to use to open the PBBA enabled CFI App.
     * @param brn         The BRN code to display.
     * @param timeoutTS   The timeout in milliseconds
     * @return The new fragment instance.
     */
    @SuppressWarnings("TypeMayBeWeakened")
    public static PBBAPopupFragment newInstance(@NonNull final String secureToken, @NonNull final String brn, final long
            timeoutTS) {
        final PBBAPopupFragment fragment = new PBBAPopupFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(KEY_SECURE_TOKEN, secureToken);
        arguments.putSerializable(KEY_BRN, brn);
        arguments.putLong(KEY_TIMEOUT, timeoutTS);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if(savedInstanceState != null){
            mBrn = savedInstanceState.getString(KEY_BRN);
            mSecureToken = savedInstanceState.getString(KEY_SECURE_TOKEN);
            mTimeoutTS = savedInstanceState.getLong(KEY_TIMEOUT);
        }else {
            mBrn = arguments.getString(KEY_BRN);
            mSecureToken = arguments.getString(KEY_SECURE_TOKEN);
            mTimeoutTS = arguments.getLong(KEY_TIMEOUT);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pbba_popup_fragment, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView pbba_popup_text_third = (TextView)view.findViewById(R.id.pbba_popup_text_third);

        final CustomTextView pbba_secure_message = (CustomTextView)view.findViewById(R.id.pbba_popup_ecomm_secure_message);
        final CustomTextView pbba_second_step = (CustomTextView)view.findViewById(R.id.pbba_ecomm_second_step_text_view);
        if (pbba_secure_message != null) {
            final AccessibilityDelegateCompat delegate = new AccessibilityDelegateCompat() {
                @Override
                public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                    info.setImportantForAccessibility(true);
                    info.setTraversalAfter(pbba_popup_text_third);
                    super.onInitializeAccessibilityNodeInfo(host, info);
                }
            };

            ViewCompat.setAccessibilityDelegate(pbba_secure_message, delegate);
        }

        ViewCompat.setImportantForAccessibility(pbba_popup_text_third,ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
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
            worker.schedule(task, 300, TimeUnit.MILLISECONDS);

            ViewCompat.setAccessibilityDelegate(image, delegate);
        }
        onCreateECommView(savedInstanceState);
        configureStepsTextMessage(view);
    }

    private void onCreateECommView(@Nullable final Bundle savedInstanceState) {
        final PBBAPopupCallback callback = getCallback();
        final long timer = savedInstanceState != null ? savedInstanceState.getLong(KEY_TIME_VALUE) : NO_TIMER_VALUE;
        if (callback != null && timer == NO_TIMER_VALUE) {
            callback.onStartTimer();
        }

        if (timer != NO_TIMER_VALUE) {
            mTimeoutTS = TimeUnit.SECONDS.toMillis(timer);
        }

        if (mTimeoutTS == EXPIRED_VALUE) {
            displayErrorExpired();
        } else {
            setTransactionInfo(mSecureToken, mBrn, mTimeoutTS);
        }
    }

    /**
     * Populate BRN screen with transaction info
     *
     * @param secureToken The secure token to use to open the PBBA enabled CFI App.
     * @param brn         The BRN code to display.
     * @param timeoutTS   The timeout in milliseconds
     */
    public void setTransactionInfo(@NonNull final String secureToken, @NonNull final String brn, long timeoutTS) {
        if (secureMessageText != null) {
            secureMessageText.setVisibility(View.VISIBLE);
        }

        if (timeoutTS != mTimeoutTS && getCallback() != null) {
            getCallback().onStartTimer();
        }

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.pbba_popup_ecomm_brn_code_timer_layout, null, false);
        countDownTimer = (TextView) view.findViewById(R.id.pbba_popup_timer_value);
        mSecureToken = secureToken;
        mBrn = brn;
        mTimeoutTS = timeoutTS;

        countDown = new CountDownTimer(mTimeoutTS, 1000) {
            @Override
            @TargetApi(android.os.Build.VERSION_CODES.LOLLIPOP)
            public void onTick(final long millisUntilFinished) {
                final long timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                if (countDownTimer != null) {
                    if (timeInSeconds <= CRITICAL_TIME_VALUE) {
                        countDownTimer.setBackgroundResource(R.drawable.pbba_popup_ecomm_count_background_red);
                    }
                    countDownTimer.setText(String.valueOf(timeInSeconds));
                    if(countDownTimer.isAccessibilityFocused())
                        countDownTimer.announceForAccessibility(String.valueOf(timeInSeconds));
                }
            }

            /**
             * Callback that timer have finished
             */
            @Override
            public void onFinish() {
                final PBBAPopupCallback callBack = getCallback();
                if (callBack != null) {
                    callBack.onEndTimer();
                }
                countDownTimer = null;
                displayErrorExpired();

            }
        }.start();

        onCreateBrnView(view, mBrn);
        if (ltThirdStepLayout != null) {
            ltThirdStepLayout.removeAllViews();
            ltThirdStepLayout.addView(view);
        }
    }

    private static void onCreateBrnView(@Nullable final View view, @NonNull final String brn) {
        if (view != null && !TextUtils.isEmpty(brn)) {
            final char[] code = brn.toCharArray();
            if (code.length == 6) {
                ((TextView) view.findViewById(R.id.code_value_1)).setText(String.valueOf(code[0]));
                ((TextView) view.findViewById(R.id.code_value_2)).setText(String.valueOf(code[1]));
                ((TextView) view.findViewById(R.id.code_value_3)).setText(String.valueOf(code[2]));
                ((TextView) view.findViewById(R.id.code_value_4)).setText(String.valueOf(code[3]));
                ((TextView) view.findViewById(R.id.code_value_5)).setText(String.valueOf(code[4]));
                ((TextView) view.findViewById(R.id.code_value_6)).setText(String.valueOf(code[5]));
            }
        }
    }

    private void displayErrorExpired() {
        setErrorMessage(getString(R.string.exception_request_expired), getString(R.string.exception_request_expired_message));
    }

    /**
     * Get the BRN code displayed in the fragment.
     *
     * @return The BRN code value.
     */
    @NonNull
    public String getBrn() {
        return mBrn;
    }

    /**
     * Get the secure token stored in the fragment.
     *
     * @return The secure token value.
     */
    @NonNull
    public String getSecureToken() {
        return mSecureToken;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, String.format("onSaveInstanceState (%s)", this));
        if (countDownTimer != null) {
            outState.putLong(KEY_TIME_VALUE, Long.parseLong(countDownTimer.getText().toString()));
            outState.putString(KEY_BRN,getBrn());
            outState.putString(KEY_SECURE_TOKEN,mSecureToken);
            outState.putLong(KEY_TIMEOUT,mTimeoutTS);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        if (countDown != null) {
            countDown.cancel();
        }
        super.onDestroyView();
    }
}
