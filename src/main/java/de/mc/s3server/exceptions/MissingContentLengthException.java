/*
 * Copyright (c) 2016 Mind Consulting UG(haftungsbeschränkt)
 */

package de.mc.s3server.exceptions;

/**
 * Created by Ralf Ulrich on 20.02.16.
 */
public class MissingContentLengthException extends S3ServerException{
    public MissingContentLengthException() {
        super("You must provide the Content-Length HTTP Header");
    }

    public MissingContentLengthException(String message) {
        super(message);
    }

    public MissingContentLengthException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingContentLengthException(Throwable cause) {
        super(cause);
    }

    public MissingContentLengthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
