package com.zapp.library.merchant.network.rest;

import com.zapp.library.merchant.network.exception.GenericException;
import com.zapp.library.merchant.network.exception.NetworkException;
import com.zapp.library.merchant.network.response.AvailableBankAppsResponse;

import java.util.List;

import retrofit.http.GET;

/**
 * REST network client interface.
 */
public interface IPBBABankLogoService {
    /**
     * URL for get available bank apps
     */
    String AVAILABLE_BANK_APP_URL = "/merchant-lib/banks.json";

    @GET(AVAILABLE_BANK_APP_URL)
    List<AvailableBankAppsResponse> getAvailableBankApps() throws NetworkException, GenericException;
}
