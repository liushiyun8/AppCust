package com.eke.cust.api;

/**
 * Created by wfpb on 15/6/25.
 */
public class RestApiException extends AssertionError {

    public RestApiException(String detailMessage) {
        super(detailMessage);
    }

    private static final long serialVersionUID = 2632591212433634947L;
}
