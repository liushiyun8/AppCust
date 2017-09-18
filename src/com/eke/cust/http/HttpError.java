/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eke.cust.http;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Response;


public class HttpError extends RuntimeException {
    public static HttpError networkError(String url, IOException exception) {
        return new HttpError(exception.getMessage(), url, null, null, Kind.NETWORK,
                exception);
    }

    public static HttpError conversionError(String url, Response response, Type successType, ConversionException exception) {
        return new HttpError(exception.getMessage(), url, response, successType,
                Kind.CONVERSION, exception);
    }

    public static HttpError httpError(String url, Response response, Type successType) {
        String message = response.code() + " " + response.message();
        return new HttpError(message, url, response, successType, Kind.HTTP, null);
    }

    public static HttpError unexpectedError(String url, Throwable exception) {
        return new HttpError(exception.getMessage(), url, null, null, Kind.UNEXPECTED,
                exception);
    }

    public enum Kind {
        /** An {@link java.io.IOException} occurred while communicating to the server. */
        NETWORK,
        /** An exception was thrown while (de)serializing a body. */
        CONVERSION,
        /** A non-200 HTTP status code was received from the server. */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    private final String url;
    private final Response response;
    private final Type successType;
    private final Kind kind;

    HttpError(String message, String url, Response response,
              Type successType, Kind kind, Throwable exception) {
        super(message, exception);
        this.url = url;
        this.response = response;
        this.successType = successType;
        this.kind = kind;
    }

    /** The request URL which produced the error. */
    public String getUrl() {
        return url;
    }

    /** Response object containing status code, headers, body, etc. */
    public Response getResponse() {
        return response;
    }

    /**
     * Whether or not this error was the result of a network error.
     *
     * @deprecated Use {@link #getKind() getKind() == Kind.NETWORK}.
     */
    @Deprecated
    public boolean isNetworkError() {
        return kind == Kind.NETWORK;
    }

    /** The event kind which triggered this error. */
    public Kind getKind() {
        return kind;
    }


    /**
     * The type declared by either the interface method return type or the generic type of the
     * supplied {@link  Callback} parameter.
     */
    public Type getSuccessType() {
        return successType;
    }

}
