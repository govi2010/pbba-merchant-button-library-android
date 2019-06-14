package com.zapp.library.merchant.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zapp.library.merchant.network.response.AvailableBankAppsResponse;

import java.util.ArrayList;
import java.util.List;

public class FilterAvailableBankAppsBySmallLogo implements FilterAvailableBankApps {
    @Override
    public List<AvailableBankAppsResponse> filter(@NonNull final List<AvailableBankAppsResponse> list) {
        List<AvailableBankAppsResponse> filteredList = new ArrayList<>();
        for (AvailableBankAppsResponse availableBankAppsResponse : list) {
            if (!TextUtils.isEmpty(availableBankAppsResponse.getSmallLogo())){
                filteredList.add(availableBankAppsResponse);
            }
        }
        return filteredList;
    }
}
