/*
 * Copyright (c) 2016 Mind Consulting UG(haftungsbeschränkt)
 */

package de.mc.s3server.authorization;

import de.mc.s3server.entities.api.S3CallContext;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.hash.Hashing.sha256;
import static com.google.common.io.BaseEncoding.base16;

/**
 *
 */
public class AwsSignatureVersion4 {
    protected static final Pattern AWS_AUTH4_PATTERN =
            Pattern.compile("AWS4-HMAC-SHA256 Credential=([^/]+)/([^/]+)/([^/]+)/s3/aws4_request, SignedHeaders=([^,"
                    + "]+), Signature=(.+)");
    public static final String HMAC_SHA_256 = "HmacSHA256";

    public boolean isV4(S3CallContext callContext) {
        return buildMatcher(callContext).matches();
    }

    private Matcher initializedMatcher(S3CallContext callContext) {
        Matcher matcher = buildMatcher(callContext);
        return matcher.matches() ? matcher : null;
    }

    private Matcher buildMatcher(S3CallContext callContext) {
        return AWS_AUTH4_PATTERN.matcher(callContext.getHeader().getAuthorization());
    }

    public String computeV4(S3CallContext callContext) throws Exception {
        final MatchResult aws4Header = initializedMatcher(callContext);

        assert aws4Header != null;
        byte[] dateKey = hmacSHA256(("AWS4" + callContext.getUser().getSecretKey()).getBytes(UTF_8), aws4Header.group(2));
        byte[] dateRegionKey = hmacSHA256(dateKey, aws4Header.group(3));
        byte[] dateRegionServiceKey = hmacSHA256(dateRegionKey, "s3");
        byte[] signingKey = hmacSHA256(dateRegionServiceKey, "aws4_request");

        byte[] signedData = hmacSHA256(signingKey, buildStringToSign(callContext));
        return base16().lowerCase().encode(signedData);
    }

    private String buildStringToSign(S3CallContext callContext) {
        final StringBuilder canonicalRequest = buildCanonicalRequest(callContext);
        final MatchResult aws4Header = initializedMatcher(callContext);
        assert aws4Header != null;
        return "AWS4-HMAC-SHA256\n"
                + callContext.getHeader().getXamzDate()
                + "\n"
                + callContext.getHeader().getXamzDate().substring(0, 8)
                + "/"
                + aws4Header.group(3)
                + "/s3/aws4_request\n"
                + hashedCanonicalRequest(canonicalRequest);
    }


    private StringBuilder buildCanonicalRequest(S3CallContext callContext) {
        final MatchResult aws4Header = initializedMatcher(callContext);
        StringBuilder canonicalRequest = new StringBuilder(callContext.getMethod());
        canonicalRequest.append("\n");
        canonicalRequest.append(callContext.getUri());
        canonicalRequest.append("\n");
        canonicalRequest.append(callContext.getQueryString());
        canonicalRequest.append("\n");

        assert aws4Header != null;
        for (String name : aws4Header.group(4).split(";")) {
            canonicalRequest.append(name.trim());
            canonicalRequest.append(":");
            canonicalRequest.append(callContext.getHeader().getFullHeader().get(name).trim());
            canonicalRequest.append("\n");
        }
        canonicalRequest.append("\n");
        canonicalRequest.append(aws4Header.group(4));
        canonicalRequest.append("\n");
        canonicalRequest.append(callContext.getHeader().getXamzContentSha256());
        return canonicalRequest;
    }

    private String hashedCanonicalRequest(final StringBuilder canonicalRequest) {
        return sha256().hashString(canonicalRequest, UTF_8).toString();
    }

    private byte[] hmacSHA256(byte[] key, String value) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, HMAC_SHA_256);
        Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(keySpec);
        return mac.doFinal(value.getBytes(UTF_8));
    }
}
