package com.zapp.library.merchant.exception;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.zapp.library.merchant.R;

import java.io.Serializable;

/**
 * Flexible error class provides type, title, message and arguments features for Zapp merchant library.
 */
public class ZappError implements Serializable {

    /**
     * The type of the error.
     */
    private ErrorType mType = ErrorType.GENERIC_INTERNAL_ERROR;

    /**
     * The custom title of the error.
     */
    private final String mCustomTitle;

    /**
     * The custom message of the error.
     */
    private final String mCustomMessage;

    /**
     * The arguments for the error.
     */
    private final String[] mArguments;

    /**
     * Create new error instance using given error type, default title and message.
     *
     * @param type The error type to use.
     */
    public ZappError(final ErrorType type) {
        this(type, null, null);
    }

    /**
     * Create new error instance using given error type, default title and message and custom arguments.
     *
     * @param type The error type to use.
     * @param args The arguments to use.
     */
    public ZappError(final ErrorType type, final String[] args) {
        this(type, null, null, args);
    }

    /**
     * Create new error instance using given error type, custom title and custom message.
     *
     * @param type          The error type to use.
     * @param customTitle   The custom title to use.
     * @param customMessage The custom message to use.
     */
    public ZappError(final ErrorType type, final String customTitle, final String customMessage) {
        this(type, customTitle, customMessage, null);
    }

    /**
     * Create new error instance using given error type, custom title, custom message and arguments.
     *
     * @param type          The error type to use.
     * @param customTitle   The custom error title to use.
     * @param customMessage The custom error message to use.
     * @param args          The arguments to use.
     */
    public ZappError(final ErrorType type, final String customTitle, final String customMessage, final String[] args) {
        if (mType == null) {
            throw new IllegalArgumentException("type == null");
        }
        mType = type;
        mCustomTitle = customTitle;
        mCustomMessage = customMessage;
        mArguments = args;
    }

    /**
     * Get the type of this error.
     *
     * @return The {@link ErrorType} error type.
     */
    public ErrorType getType() {
        return mType;
    }

    /**
     * Get the language specific error message for this error.
     *
     * @param context The context to use.
     * @return The {@link String} error message for this error.
     */
    public String getErrorMessage(@Nullable final Context context) {
        if (mCustomMessage != null) {
            return mCustomMessage;
        }
        if (context == null) {
            return null;
        }

        final String errorMessage;
        switch (mType) {
            case NETWORK_ERROR:
                errorMessage = context.getString(R.string.zapp_error_message_network_error);
                break;
            case GENERIC_INTERNAL_ERROR:
            default:
                errorMessage = context.getString(R.string.zapp_error_msg_generic_internal_error);
                break;
        }
        return errorMessage;
    }

    /**
     * Get the language specific error title for this error.
     *
     * @param context The context to use.
     * @return The {@link String} error title for this error.
     */
    public String getErrorTitle(@Nullable final Context context) {
        final String title;
        if (mCustomTitle != null) {
            title = mCustomTitle;
        } else if (context == null) {
            title = null;
        } else {
            title = context.getString(R.string.zapp_error_title_generic);
        }
        return title;
    }

    @Override
    public String toString() {
        final String arguments = mArguments == null ? "N/A" : TextUtils.join(",", mArguments);
        return String.format("ZappError[type:%s, customTitle:'%s', customMessage:'%s', arguments:[%s]]", mType.name(), mCustomTitle, mCustomMessage,
                arguments);
    }
}
