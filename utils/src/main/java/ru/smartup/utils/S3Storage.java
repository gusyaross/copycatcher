package ru.smartup.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Component;
import ru.smartup.utils.config.CloudConfig;
import ru.smartup.utils.config.S3Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**S3 storage client wrapper. This class provides operations on yandex cloud storage*/
@Component
public class S3Storage {
    private final S3Config s3Config;
    private final AmazonS3 s3;

    public S3Storage(CloudConfig cloudConfig, AmazonS3 s3) {
        s3Config = cloudConfig.getS3();
        this.s3 = s3;
    }

    /**<p>
     * putObject is a method to put json string into s3 storage by key
     * </p>
     *
     * @param key
     *      id which identifies object in storage
     * @param jsonObject
     *      object which is going to be stored in storage*/
    public void putObject(String key, String jsonObject) {
        s3.putObject(s3Config.getBucket(), key, jsonObject);
    }

    /**<p>
     * getObject is a method to put json string into s3 storage by key
     * </p>
     *
     * @param key
     *      id by which client gets json object from storage
     * @throws IOException*/
    public String getObject(String key) throws IOException {
        S3Object s3Object = s3.getObject(s3Config.getBucket(), key);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()))) {
            return br.readLine();
        }
    }
}
