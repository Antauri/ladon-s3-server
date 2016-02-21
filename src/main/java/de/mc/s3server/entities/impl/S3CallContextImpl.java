/*
 * Copyright (c) 2016 Mind Consulting UG(haftungsbeschränkt)
 */

package de.mc.s3server.entities.impl;

import de.mc.s3server.entities.api.*;
import de.mc.s3server.exceptions.InternalErrorException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ralf Ulrich on 17.02.16.
 */
public class S3CallContextImpl implements S3CallContext {

    private SecurityContext securityContext;
    private HttpServletResponse response;
    private S3RequestHeader header;
    private S3RequestParams params;
    private String requestId = UUID.randomUUID().toString();

    public S3CallContextImpl(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response, Map<String, String[]> params) {
        this.securityContext = securityContext;
        this.header = new S3RequestHeaderImpl(request);
        this.params = new S3RequestParamsImpl(params);
        this.response = response;
    }

    @Override
    public void setResponseHeader(S3ResponseHeader responseHeader) {
        S3ResponseHeaderImpl.appendHeaderToResponse(response, (S3ResponseHeaderImpl) responseHeader);
    }

    @Override
    public S3RequestId getRequestId() {
        return () -> requestId;
    }
    @Override
    public void setContent(InputStream inputStream) {
        try(InputStream in = inputStream) {
            StreamUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new InternalErrorException("", getRequestId());
        }
    }

    @Override
    public S3RequestHeader getHeader() {
        return header;
    }

    @Override
    public S3RequestParams getParams() {
        return params;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }
}
