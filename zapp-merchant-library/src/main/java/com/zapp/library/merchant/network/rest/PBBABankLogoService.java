package com.zapp.library.merchant.network.rest;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.zapp.library.merchant.network.exception.GenericException;
import com.zapp.library.merchant.network.exception.NetworkException;
import com.zapp.library.merchant.network.response.AvailableBankAppsDeserializer;
import com.zapp.library.merchant.util.PBBALibraryUtils;

import java.lang.reflect.Type;
import java.util.List;

import retrofit.Endpoint;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Provider for the REST network client
 */
public class PBBABankLogoService {
    /**
     * The dynamic endpoint
     */
    private static Endpoint sEndpoint;

    /**
     * Get new rest client instance
     *
     * @return The {@link IPBBABankLogoService} REST client instance
     */
    @SuppressWarnings("SynchronizedMethod")
    public static synchronized IPBBABankLogoService getInstance(@NonNull final Context context) {
        if (sEndpoint == null) {
            sEndpoint = new Endpoint() {

                @Override
                public String getUrl() {
                    return PBBALibraryUtils.getCfiLogosCDNPath(context);
                }

                @Override
                public String getName() {
                    return "";
                }
            };
        }
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(sEndpoint)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(final RequestInterceptor.RequestFacade requestFacade) {
                        requestFacade.addHeader("Connection", "close");
                        requestFacade.addHeader("Accept-Charset", "utf-8");
                    }
                })
                .setConverter(new Converter() {
                    @Override
                    public Object fromBody(final TypedInput body, final Type type) throws ConversionException {
                        final GsonBuilder gsonBuilder = new GsonBuilder();
                        gsonBuilder.registerTypeAdapter(List.class, new AvailableBankAppsDeserializer());
                        final Converter converter = new GsonConverter(gsonBuilder.create());

                        return converter.fromBody(body, type);
                    }

                    @Override
                    public TypedOutput toBody(final Object object) {
                        final GsonBuilder gsonBuilder = new GsonBuilder();
                        return new GsonConverter(gsonBuilder.create()).toBody(object);
                    }
                })
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(@NonNull final RetrofitError cause) {

                        final Throwable error;
                        if (cause.getKind() == RetrofitError.Kind.NETWORK) {
                            error = new NetworkException();
                        } else {
                            error = new GenericException();
                        }

                        return error;
                    }
                })
                .build();

        return restAdapter.create(IPBBABankLogoService.class);
    }
}
