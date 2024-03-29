package io.github.devlibx.easy.flink.utils;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.io.IOUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Objects;

public class ConfigReader {

    /**
     * Read config from S3 url and build parameter tool
     */
    public static ParameterTool readConfigsFromFile(String fileUrl, boolean failOnError) throws IOException {
        try {
            return ParameterTool.fromPropertiesFile(new File(fileUrl));
        } catch (IOException e) {
            if (failOnError) {
                throw e;
            } else {
                return ParameterTool.fromSystemProperties();
            }
        }
    }

    /**
     * Read config from S3 url and build parameter tool
     */
    public static String readConfigsFromFileAsString(String fileUrl, boolean failOnError) throws IOException {
        try {
            return IOUtils.toString(Files.newInputStream(new File(fileUrl).toPath()), Charset.defaultCharset());
        } catch (IOException e) {
            if (failOnError) {
                throw e;
            } else {
                return "";
            }
        }
    }

    /**
     * Read config from S3 url and build parameter tool
     */
    public static ParameterTool readConfigsFromS3(String s3Url, boolean failOnError) throws IOException {
        try {
            AmazonS3URI uri = new AmazonS3URI(s3Url);
            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
            S3Object object = s3Client.getObject(new GetObjectRequest(uri.getBucket(), uri.getKey()));

            InputStream objectData = object.getObjectContent();
            ParameterTool parameter = ParameterTool.fromPropertiesFile(objectData);
            objectData.close();
            return parameter;
        } catch (IOException e) {
            if (failOnError) {
                throw e;
            } else {
                return ParameterTool.fromSystemProperties();
            }
        }
    }

    /**
     * Read config from S3 url and build configs
     */
    public static String readConfigsFromS3AsString(String s3Url, boolean failOnError) throws IOException {
        try {
            AmazonS3URI uri = new AmazonS3URI(s3Url);
            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
            S3Object object = s3Client.getObject(new GetObjectRequest(uri.getBucket(), uri.getKey()));
            InputStream objectData = object.getObjectContent();
            return IOUtils.toString(objectData, Charset.defaultCharset());
        } catch (IOException e) {
            if (failOnError) {
                throw e;
            } else {
                return "";
            }
        }
    }

    /**
     * Helper to find the offset from property file
     */
    public static OffsetsInitializer getOffsetsInitializer(ParameterTool parameters) {
        String offset = parameters.get("startingOffsets", "earliest");
        if (Objects.equals("earliest", offset)) {
            return OffsetsInitializer.earliest();
        } else if (Objects.equals("latest", offset)) {
            return OffsetsInitializer.latest();
        } else if (Objects.equals("committedOffsetsLatest", offset)) {
            return OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST);
        } else if (Objects.equals("committedOffsetsEarliest", offset)) {
            return OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST);
        } else if (Objects.equals("committedOffsetsNone", offset)) {
            return OffsetsInitializer.committedOffsets(OffsetResetStrategy.NONE);
        } else if (Objects.equals("timestamp", offset)) {
            return OffsetsInitializer.timestamp(parameters.getLong("startingOffsetsTimestamp", 0));
        } else {
            return OffsetsInitializer.latest();
        }
    }

    /**
     * Helper to find the offset from property file
     */
    public static OffsetsInitializer getOffsetsInitializer(String offset, long startingOffsetsTimestamp) {
        if (Objects.equals("earliest", offset)) {
            return OffsetsInitializer.earliest();
        } else if (Objects.equals("latest", offset)) {
            return OffsetsInitializer.latest();
        } else if (Objects.equals("committedOffsetsLatest", offset)) {
            return OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST);
        } else if (Objects.equals("committedOffsetsEarliest", offset)) {
            return OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST);
        } else if (Objects.equals("committedOffsetsNone", offset)) {
            return OffsetsInitializer.committedOffsets(OffsetResetStrategy.NONE);
        } else if (Objects.equals("timestamp", offset)) {
            return OffsetsInitializer.timestamp(startingOffsetsTimestamp);
        } else {
            return OffsetsInitializer.latest();
        }
    }
}
