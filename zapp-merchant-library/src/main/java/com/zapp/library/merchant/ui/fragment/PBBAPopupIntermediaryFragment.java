package com.zapp.library.merchant.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zapp.library.merchant.R;
import com.zapp.library.merchant.ui.PBBAPopupCallback;
import com.zapp.library.merchant.ui.view.CustomTextView;
import com.zapp.library.merchant.util.PBBAAppUtils;
import com.zapp.library.merchant.util.PBBALibraryUtils;

import static com.zapp.library.merchant.util.PBBALibraryUtils.CFI_APP_NAME_DEFAULT;

/**
 * Popup fragment for Zapp electronic and mobile commerce (m-Comm) journeys.
 */
public final class PBBAPopupIntermediaryFragment extends PBBAPopup {

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

    public static PBBAPopupIntermediaryFragment newInstance(@NonNull final String secureToken, @NonNull final String brn, final long timeoutTS) {
        final PBBAPopupIntermediaryFragment fragment = new PBBAPopupIntermediaryFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(KEY_SECURE_TOKEN, secureToken);
        arguments.putSerializable(KEY_BRN, brn);
        arguments.putLong(KEY_TIMEOUT, timeoutTS);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        mBrn = arguments.getString(KEY_BRN);
        mSecureToken = arguments.getString(KEY_SECURE_TOKEN);
        mTimeoutTS = arguments.getLong(KEY_TIMEOUT);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View content = inflater.inflate(R.layout.pbba_popup_fragment_mcomm, container, false);


        final boolean useDefaultCfiAppName = PBBALibraryUtils.getCfiAppName(getContext()) == CFI_APP_NAME_DEFAULT;

        final String cfiAppName = useDefaultCfiAppName ? getString(R.string.pbba_name) : getString(R.string.pingit);

        final CustomTextView openBankingAppCallToAction = (CustomTextView) content.findViewById(R.id
                .pbba_popup_open_banking_app_call_to_action);
        if (openBankingAppCallToAction != null) {
            openBankingAppCallToAction.setText(getString(R.string.pbba_popup_open_banking_app_call_to_action, cfiAppName));
        }

        final CustomTextView payWithAnotherDeviceCallToAction = (CustomTextView) content.findViewById(R.id.pbba_popup_pay_with_another_device_call_to_action);
        if (payWithAnotherDeviceCallToAction != null) {
            final String cfiAppName1 = useDefaultCfiAppName ? getString(R.string.pbba_name_other_device) : getString(R.string.pingit);
            payWithAnotherDeviceCallToAction.setText(getString(R.string.pbba_popup_pay_with_another_device_call_to_action, cfiAppName1));
        }

        final TextView openBankingAppButtonTitle = (TextView) content.findViewById(R.id.pbba_button_open_banking_app_text);
        if (openBankingAppButtonTitle != null) {
            if (useDefaultCfiAppName) {
                openBankingAppButtonTitle.setText(getString(R.string.pbba_button_text_open_banking_app));
            } else {
                openBankingAppButtonTitle.setText(getString(R.string.pbba_button_text_open_banking_app_co_branded));
            }
        }

        onCreateMCommView(content);
        return content;

    }

    private void onCreateMCommView(@NonNull final View view) {
        final View openBankingAppButton = view.findViewById(R.id.pbba_button_open_banking_app);
        openBankingAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Activity activity = getActivity();
                if (activity != null) {
                    PBBALibraryUtils.setOpenBankingAppButtonClicked(getContext(), true);
                    if (PBBAAppUtils.isCFIAppAvailable(activity)) {
                        dismiss();
                        PBBAAppUtils.openBankingApp(activity, mSecureToken);
                    } else {
                        onDisplayBrn();
                    }
                }
            }
        });

        final View getCodeButton = view.findViewById(R.id.pbba_button_get_code);
        if (getCodeButton != null) {
            //'Get Code' button appears on phone layout only
            getCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onDisplayBrn();
                }
            });
        }

        final LinearLayout moreAboutContianer = (LinearLayout) view.findViewById(R.id.pbba_popup_more_about_container);

        moreAboutContianer.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final FragmentActivity activity = getActivity();
                PBBAAppUtils.showPBBAPopupAbout(activity);
            }
        });

    }

    private void onDisplayBrn() {
        final FragmentActivity activity = getActivity();
        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final PBBAPopupFragment newFragment = PBBAPopupFragment.newInstance(mSecureToken, mBrn, mTimeoutTS);
        final PBBAPopupCallback callback = getCallback();
        if (callback != null) {
            newFragment.setCallback(callback);
        }
        fragmentManager.beginTransaction().remove(this).add(newFragment, TAG).commit();
    }


    @Override
    protected boolean shouldLoadAvailableBankApps() {
        return false;
    }
}
