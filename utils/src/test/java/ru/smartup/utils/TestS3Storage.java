package ru.smartup.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import ru.smartup.utils.config.CloudConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CloudConfig.class})
@ConfigurationPropertiesScan("classpath:application.properties")
public class TestS3Storage {
    private S3Storage s3Storage;

    @Autowired
    private CloudConfig cloudConfig;

    private final AmazonS3 s3 = Mockito.mock(AmazonS3.class);

    @BeforeEach
    public void setUp() {
        s3Storage = new S3Storage(cloudConfig, s3);
    }

    @Test
    public void testPutObject() {
        assertDoesNotThrow(() -> s3Storage.putObject("key1", "content"));
        verify(s3).putObject(cloudConfig.getS3().getBucket(), "key1", "content");
    }

    @Test
    public void testGetObject() throws IOException {
        String key = "key";
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(new S3ObjectInputStream(new ByteArrayInputStream(key.getBytes()), null));
        when(s3.getObject(cloudConfig.getS3().getBucket(), key)).thenReturn(s3Object);
        assertEquals(key, s3Storage.getObject("key"));
        verify(s3).getObject(cloudConfig.getS3().getBucket(), key);
    }
}
