package com.zapp.library.merchant.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.zapp.library.merchant.R;
import com.zapp.library.merchant.exception.ErrorType;
import com.zapp.library.merchant.exception.ZappError;
import com.zapp.library.merchant.network.exception.GenericException;
import com.zapp.library.merchant.network.exception.NetworkException;
import com.zapp.library.merchant.network.response.AvailableBankAppsResponse;
import com.zapp.library.merchant.network.response.PBBABankLogoResponse;
import com.zapp.library.merchant.network.rest.IPBBABankLogoService;
import com.zapp.library.merchant.network.rest.PBBABankLogoService;
import com.zapp.library.merchant.util.FilterAvailableBankApps;

import java.util.Collections;
import java.util.List;

/**
 * Async loader to get available bank apps
 */
public class AvailableBankAppsLoader extends AsyncTaskLoader<PBBABankLogoResponse> {

    /**
     * Tag for Logging
     */
    private static final String TAG = AvailableBankAppsLoader.class.getSimpleName();
    private static final int MAX_COUNT = 8;
    private final FilterAvailableBankApps filterAvailableBankApps;

    /**
     * The gateway response.
     */
    private PBBABankLogoResponse mPBBABankLogoResponse = null;

    /**
     * Create new instance
     *
     * @param context The context to use.
     */
    public AvailableBankAppsLoader(@NonNull final Context context, @NonNull final FilterAvailableBankApps filterAvailableBankApps) {
        super(context);
        this.filterAvailableBankApps = filterAvailableBankApps;
    }

    @Override
    public PBBABankLogoResponse loadInBackground() {
        final Context context = getContext().getApplicationContext();
        List<AvailableBankAppsResponse> response = null;
        ZappError error = null;
        try {
            final IPBBABankLogoService gateway = PBBABankLogoService.getInstance(context);
            response = gateway.getAvailableBankApps();
            if (response != null) {
                response = filterAvailableBankApps.filter(response);
                if (response.size() > MAX_COUNT) {
                    response = response.subList(0, MAX_COUNT);
                }
                Collections.shuffle(response);
            }
        } catch (GenericException e) {
            Log.w(TAG, e.getMessage(), e);
            error = new ZappError(ErrorType.GENERIC_INTERNAL_ERROR);
        } catch (NetworkException e) {
            Log.w(TAG, e.getMessage(), e);
            error = new ZappError(ErrorType.NETWORK_ERROR, context.getString(R.string.exception_network_title),
                    context.getString(R.string.exception_no_network_msg));
        }
        mPBBABankLogoResponse = new PBBABankLogoResponse(response, error);
        return mPBBABankLogoResponse;
    }

    @Override
    protected void onStartLoading() {
        Log.e(TAG, String.format("onStartLoading (%s)", this));
        if (mPBBABankLogoResponse != null) {
            deliverResult(mPBBABankLogoResponse);
            return;
        }

        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        Log.e(TAG, String.format("onStopLoading (%s)", this));
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        Log.e(TAG, String.format("onReset (%s)", this));
        onStopLoading();
    }

    @Override
    public void deliverResult(PBBABankLogoResponse data) {
        if (isReset()) {
            Log.e(TAG, String.format("deliverResult (%s): ignored due to reset", this));
            return;
        }

        if (isStarted()) {
            super.deliverResult(data);
            Log.e(TAG, String.format("deliverResult (%s): delivering result: %s", this, data));
        } else {
            Log.e(TAG, String.format("deliverResult (%s): ignored due to stopped state or nothing to deliver", this));
        }
    }
}
