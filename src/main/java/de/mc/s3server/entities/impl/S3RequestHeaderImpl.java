/*
 * Copyright (c) 2016 Mind Consulting UG(haftungsbeschränkt)
 */

package de.mc.s3server.entities.impl;

import de.mc.s3server.entities.api.S3ACL;
import de.mc.s3server.entities.api.S3RequestHeader;

import javax.servlet.http.HttpServletRequest;

import static de.mc.s3server.common.S3Constants.*;

/**
 * @author Ralf Ulrich on 20.02.16.
 */
public class S3RequestHeaderImpl implements S3RequestHeader {

    private HttpServletRequest request;

    public S3RequestHeaderImpl(HttpServletRequest request) {
        this.request = request;
    }


    private String getHeader(String key) {
        return request.getHeader(key);
    }

//    private Object getAttribute(String key) {
//        return request.getAttribute(key);
//    }

    @Override
    public Long getContentLength() {
        String length = getHeader(CONTENT_LENGTH);
        return length != null ? Long.parseLong(length) : null;
    }

    @Override
    public String getContentType() {
        return getHeader(CONTENT_TYPE);
    }

    @Override
    public String getContentMD5() {
        return getHeader(CONTENT_MD5);
    }

    @Override
    public String getXamzContentSha256() {
        return getHeader(X_AMZ_CONTENT_SHA256);
    }

    @Override
    public String getConnection() {
        return getHeader(CONNECTION);
    }

    @Override
    public String getDate() {
        return getHeader(DATE);
    }

    @Override
    public String getXamzDate() {
        return getHeader(X_AMZ_DATE);
    }

    @Override
    public String getXamzSecurityToken() {
        return getHeader(X_AMZ_SECURITY_TOKEN);
    }

    @Override
    public String getEtag() {
        return getHeader(ETAG);
    }

    @Override
    public String getServer() {
        return getHeader(SERVER);
    }

    @Override
    public String getXamzDeleteMarker() {
        return getHeader(X_AMZ_DELETE_MARKER);
    }

    @Override
    public String getXamzRequestId() {
        return getHeader(X_AMZ_REQUEST_ID);
    }

    @Override
    public String getXamzVersionId() {
        return getHeader(X_AMZ_VERSION_ID);
    }

    @Override
    public String getAuthorization() {
        return getHeader(AUTHORIZAZION);
    }

    @Override
    public String getExpect() {
        return getHeader(AUTHORIZAZION);
    }

    @Override
    public String getHost() {
        return getHeader(HOST);
    }

    @Override
    public String getExpires() {
        return getHeader(EXPIRES);
    }

    @Override
    public String getCacheControl() {
        return getHeader(CACHE_CONTROL);
    }

    @Override
    public String getContentDisposition() {
        return getHeader(CONTENT_DISPOSITION);
    }

    @Override
    public String getContentEncoding() {
        return getHeader(CONTENT_ENCODING);
    }

    @Override
    public String getStorageClass() {
        return getHeader(X_AMZ_STORAGE_CLASS);
    }

    @Override
    public S3ACL getAcl() {
        return new S3ACLImpl(
                getHeader(X_AMZ_ACL),
                getHeader(X_AMZ_GRANT_READ),
                getHeader(X_AMZ_GRANT_WRITE),
                getHeader(X_AMZ_GRANT_READ_ACP),
                getHeader(X_AMZ_GRANT_WRITE_ACP),
                getHeader(X_AMZ_GRANT_FULL_CONTROL));
    }
}
