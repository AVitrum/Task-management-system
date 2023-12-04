package com.vitrum.api.config;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {

    @Value("${accessKey}")
    private String accessKey;

    @Value("${secret}")
    private String secret;

    @Value("${region}")
    private String region;

    @Bean
    public AmazonS3 initS3Client(){
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secret);
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
