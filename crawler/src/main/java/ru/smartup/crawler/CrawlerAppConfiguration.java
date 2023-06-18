package ru.smartup.crawler;

import lombok.Setter;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@Setter
class CrawlerAppConfiguration {
    @Value("${pool.core-size}")
    private int coreSize;
    @Value("${pool.max-size}")
    private int maxSize;
    @Value("${pool.keep-alive-time}")
    private long keepAliveTime;
    @Value("${pool.queue-capacity}")
    private int queueCapacity;

    @Value("${max-http-connections}")
    private int maxHttpConnections;

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueCapacity));
    }

    @Bean
    public CloseableHttpClient httpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxHttpConnections);
        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();
    }
}
