/*
 * Copyright (c) 2016 Mind Consulting UG(haftungsbeschränkt)
 */

package de.mc.s3server.entities.impl;

import de.mc.s3server.entities.api.S3RequestParams;

import java.util.Map;

import static de.mc.s3server.common.S3Constants.*;

/**
 * Created by Ralf Ulrich on 20.02.16.
 */
public class S3RequestParamsImpl implements S3RequestParams {

    private String delimiter;
    private String encodingType;
    private String marker;
    private Integer maxKeys;
    private String prefix;

    public S3RequestParamsImpl(Map<String, String[]> requestParams) {
        this.delimiter = getFirstOrNull(DELIMITER, requestParams);
        this.encodingType = getFirstOrNull(ENCODING_TYPE, requestParams);
        this.marker = getFirstOrNull(MARKER, requestParams);
        try {
            this.maxKeys = Integer.parseInt(getFirstOrNull(MAX_KEYS, requestParams));
        } catch (NumberFormatException e) {
            //hack
        }
        this.prefix = getFirstOrNull(PREFIX, requestParams);
    }

    private String getFirstOrNull(String key, Map<String, String[]> requestParams) {
        String[] values = requestParams.get(key);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }


    @Override
    public String getDelimiter() {
        return delimiter;
    }

    @Override
    public String getEncodingType() {
        return encodingType;
    }

    @Override
    public String getMarker() {
        return marker;
    }

    @Override
    public Integer getMaxKeys() {
        return maxKeys;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }
}
