/*
 * Copyright (c) 2016 Mind Consulting UG(haftungsbeschränkt)
 */

package de.mc.s3server.exceptions;

import de.mc.s3server.entities.api.S3RequestId;

/**
 * @author Ralf Ulrich on 20.02.16.
 */
public class SignatureDoesNotMatchException extends S3ServerException {
    public SignatureDoesNotMatchException(String resource, S3RequestId requestId) {
        super("The request signature we calculated " +
                "does not match the signature you provided.", resource, requestId);
    }


}
