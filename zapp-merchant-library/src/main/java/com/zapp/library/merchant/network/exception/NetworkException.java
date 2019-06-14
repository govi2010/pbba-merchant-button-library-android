package com.zapp.library.merchant.network.exception;

/**
 * Network exception.
 */
public class NetworkException extends Exception {

    /**
     * Create new instance.
     */
    public NetworkException() {
        super("Network connection not available. Please try to connect before using.");
    }
}
