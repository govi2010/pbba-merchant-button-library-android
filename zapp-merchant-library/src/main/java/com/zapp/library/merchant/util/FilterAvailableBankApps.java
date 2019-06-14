package com.zapp.library.merchant.util;

import android.support.annotation.NonNull;

import com.zapp.library.merchant.network.response.AvailableBankAppsResponse;

import java.util.List;

public interface FilterAvailableBankApps {
    List<AvailableBankAppsResponse> filter(@NonNull final List<AvailableBankAppsResponse> list);
}
