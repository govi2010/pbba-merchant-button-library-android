package com.zapp.library.merchant.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zapp.library.merchant.R;
import com.zapp.library.merchant.network.response.PBBABankLogoResponse;

/**
 * Pop up fragment for PBBA about screen
 */
public final class PBBAPopupAboutFragment extends PBBAPopup implements LoaderManager.LoaderCallbacks<PBBABankLogoResponse> {

    /**
     * Create new instance of Pay by Bank app About Popup fragment
     *
     * @return The new fragment instance.
     */
    public static PBBAPopupAboutFragment newInstanceAbout() {
        return new PBBAPopupAboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pbba_popup_about, container, false);
    }

}
