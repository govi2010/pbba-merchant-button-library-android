package com.zapp.library.merchant.network.response;

import android.support.annotation.Nullable;

import com.zapp.library.merchant.exception.ZappError;

import java.util.List;

/**
 * PBBA bank Logo service response
 */
public class PBBABankLogoResponse {
    private final List<AvailableBankAppsResponse> availableBankAppsResponseList;
    private final ZappError zappError;

    public PBBABankLogoResponse(@Nullable final List<AvailableBankAppsResponse> availableBankAppsResponseList, @Nullable final ZappError
            zappError) {
        this.availableBankAppsResponseList = availableBankAppsResponseList;
        this.zappError = zappError;
    }

    public List<AvailableBankAppsResponse> getAvailableBankAppsResponseList() {
        return availableBankAppsResponseList;
    }

    public ZappError getZappError() {
        return zappError;
    }
}
