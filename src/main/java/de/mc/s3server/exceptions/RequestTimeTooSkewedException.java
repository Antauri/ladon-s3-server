/*
 * Copyright (c) 2016 Mind Consulting UG(haftungsbeschränkt)
 */

package de.mc.s3server.exceptions;

import de.mc.s3server.entities.api.S3RequestId;

/**
 * @author Ralf Ulrich on 20.02.16.
 */
public class RequestTimeTooSkewedException extends S3ServerException {
    public RequestTimeTooSkewedException(String resource, S3RequestId requestId) {
        super("The difference between the request time and the server's time is too large.", resource, requestId);
    }


}
