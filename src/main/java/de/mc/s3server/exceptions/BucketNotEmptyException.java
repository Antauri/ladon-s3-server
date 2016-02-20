/*
 * Copyright (c) 2016 Mind Consulting UG(haftungsbeschränkt)
 */

package de.mc.s3server.exceptions;

/**
 * Created by Ralf Ulrich on 20.02.16.
 */
public class BucketNotEmptyException extends S3ServerException {

    public BucketNotEmptyException() {
        super("The bucket you tried to delete is not empty.");
    }

    public BucketNotEmptyException(String message) {
        super(message);
    }

    public BucketNotEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public BucketNotEmptyException(Throwable cause) {
        super(cause);
    }

    public BucketNotEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
