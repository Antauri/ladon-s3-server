/*
 * Copyright (c) 2016 Mind Consulting UG(haftungsbeschränkt)
 */

package de.mc.s3server.entities.impl;

import de.mc.s3server.entities.api.S3UserMetadata;

import java.util.HashMap;

/**
 * @author Ralf Ulrich on 21.02.16.
 */
public class S3UserMetadataImpl extends HashMap<String, String> implements S3UserMetadata {
    @Override
    public String get(String key) {
        return super.get(key);
    }
}
