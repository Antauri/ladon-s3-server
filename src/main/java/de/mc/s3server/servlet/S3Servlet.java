/*
 * Copyright (c) 2016 Mind Consulting UG(haftungsbeschränkt)
 */

package de.mc.s3server.servlet;

import com.google.common.base.Charsets;
import de.mc.s3server.entities.api.S3CallContext;
import de.mc.s3server.entities.api.S3RequestId;
import de.mc.s3server.entities.impl.S3CallContextImpl;
import de.mc.s3server.entities.impl.S3UserImpl;
import de.mc.s3server.exceptions.InternalErrorException;
import de.mc.s3server.exceptions.NotImplementedException;
import de.mc.s3server.exceptions.S3ServerException;
import de.mc.s3server.jaxb.entities.*;
import de.mc.s3server.jaxb.entities.Error;
import de.mc.s3server.jaxb.mapper.ResponseWrapper;
import de.mc.s3server.repository.api.S3Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * @author Ralf Ulrich on 27.02.16.
 */
public class S3Servlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(S3Servlet.class);

    private JAXBContext jaxbContext;


    private S3Repository repository;

    private enum S3Call {
        listmybuckets,
        listbucket,
        putbucket,
        putobject,
        postbucket,
        postobject,
        getobject,
        headobject,
        headbucket,
        deletebucket,
        deleteobject
    }


    public S3Servlet(S3Repository repository) {
        this.repository = repository;
        try {
            jaxbContext = JAXBContext.newInstance(
                    Bucket.class,
                    Contents.class,
                    CreateBucketConfiguration.class,
                    Error.class,
                    ListAllMyBucketsResult.class,
                    ListBucketResult.class,
                    Owner.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String bucket = getBucketName(req);
        String objectKey = getObjectKey(req);
        if (bucket == null) {
            dispatch(S3Call.listmybuckets, req, resp, null, null);
        } else {
            if (objectKey == null) {
                dispatch(S3Call.listbucket, req, resp, bucket, null);
            } else {
                dispatch(S3Call.getobject, req, resp, bucket, objectKey);
            }
        }
    }

    private void dispatch(S3Call call, HttpServletRequest req, HttpServletResponse resp, String bucketName, String objectkey) {
        S3CallContext context = new S3CallContextImpl(req, resp,new S3UserImpl("DEFAULT", "SYSTEM"), req.getParameterMap());
        S3RequestId requestId = context.getRequestId();

        try {
            try {
                switch (call) {
                    case listmybuckets:
                        writeXmlResponse(ResponseWrapper.listAllMyBucketsResult(context.getUser(),
                                repository.listAllBuckets(context)),
                                resp,
                                HttpServletResponse.SC_OK);
                        break;
                    case listbucket:
                        writeXmlResponse(ResponseWrapper.listBucketResult(context, repository.listBucket(context, bucketName)),
                                resp,
                                HttpServletResponse.SC_OK);
                        break;
                    case putbucket:
                        CreateBucketConfiguration config;
                        try (InputStream in = req.getInputStream()) {
                            config = (CreateBucketConfiguration) getUnmarshaller().unmarshal(in);
                        } catch (JAXBException e) {
                            config = new CreateBucketConfiguration("STANDARD");
                        }
                        repository.createBucket(context, bucketName, config);
                        break;
                    case putobject:
                        repository.createObject(context, bucketName, objectkey);
                        break;
                    case postbucket:
                        throw new NotImplementedException(bucketName, requestId);
                    case postobject:
                        throw new NotImplementedException(bucketName, requestId);
                    case getobject:
                        repository.getObject(context, bucketName, objectkey, false);
                        break;
                    case headobject:
                        repository.getObject(context, bucketName, objectkey, true);
                        break;
                    case headbucket:
                        repository.getBucket(context, bucketName);
                        break;
                    case deletebucket:
                        repository.deleteBucket(context, bucketName);
                        resp.setStatus(HttpURLConnection.HTTP_NO_CONTENT);
                        break;
                    case deleteobject:
                        repository.deleteObject(context, bucketName, objectkey);
                        resp.setStatus(HttpURLConnection.HTTP_NO_CONTENT);
                        break;
                }
            } catch (JAXBException e) {
                logger.error("error procession xml " + requestId.get(), e);
                throw new InternalErrorException(objectkey, requestId);
            } catch (IOException e) {
                logger.error("error procession stream " + requestId.get(), e);
                throw new InternalErrorException(objectkey, requestId);
            }
        } catch (S3ServerException e) {
            try {
                writeXmlResponse(new Error(e), resp, e.getResponseStatus());
            } catch (Exception e1) {
                logger.error("Error writing error response " + requestId.get(), e1);
            }
        }


    }


    private void writeXmlResponse(Object content, HttpServletResponse resp, int status) throws JAXBException, IOException {
        resp.setContentType("application/xml");
        resp.setStatus(status);
        resp.setCharacterEncoding(Charsets.UTF_8.displayName());
        getMarshaller().marshal(content, resp.getOutputStream());
        resp.flushBuffer();
    }


    private Unmarshaller getUnmarshaller() throws JAXBException {
        return jaxbContext.createUnmarshaller();
    }

    private Marshaller getMarshaller() throws JAXBException {

        Marshaller m = jaxbContext.createMarshaller();
       // m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return m;
    }


    private String getBucketName(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            return null;
        }
        String[] elements = pathInfo.split("/");
        if (elements.length < 2) {
            return null;
        } else {
            return elements[1];
        }
    }


    private String getObjectKey(HttpServletRequest req) {
        String path = req.getPathInfo();
        String bucketName = getBucketName(req);
        if (bucketName != null) {
            int bucketOffset = bucketName.length() + 2;
            if (path.length() > bucketOffset) {
                return req.getPathInfo().substring(bucketOffset);
            }
        }
        return null;
    }


    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String bucket = getBucketName(req);
        String objectKey = getObjectKey(req);
        if (bucket != null) {
            if (objectKey == null) {
                dispatch(S3Call.headbucket, req, resp, bucket, null);
            } else {
                dispatch(S3Call.headobject, req, resp, bucket, objectKey);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String bucket = getBucketName(req);
        String objectKey = getObjectKey(req);
        if (bucket != null) {
            dispatch(S3Call.postobject, req, resp, bucket, objectKey);
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String bucket = getBucketName(req);
        String objectKey = getObjectKey(req);
        if (bucket != null) {
            if (objectKey == null) {
                dispatch(S3Call.putbucket, req, resp, bucket, null);
            } else {
                dispatch(S3Call.putobject, req, resp, bucket, objectKey);
            }
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String bucket = getBucketName(req);
        String objectKey = getObjectKey(req);
        if (bucket != null) {
            if (objectKey == null) {
                dispatch(S3Call.deletebucket, req, resp, bucket, null);
            } else {
                dispatch(S3Call.deleteobject, req, resp, bucket, objectKey);
            }
        }
    }


}