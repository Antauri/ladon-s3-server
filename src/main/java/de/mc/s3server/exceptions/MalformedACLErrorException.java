/*
 * Copyright (c) 2016 Mind Consulting UG(haftungsbeschränkt)
 */

package de.mc.s3server.exceptions;

/**
 * Created by Ralf Ulrich on 20.02.16.
 */
public class MalformedACLErrorException extends S3ServerException {
    public MalformedACLErrorException() {
        super("The XML you provided was not well formed or did not validate against our published schema.");
    }

    public MalformedACLErrorException(String message) {
        super(message);
    }

    public MalformedACLErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedACLErrorException(Throwable cause) {
        super(cause);
    }

    public MalformedACLErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
