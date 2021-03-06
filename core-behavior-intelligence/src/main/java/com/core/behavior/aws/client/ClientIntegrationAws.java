/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.aws.client;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.EncryptedPutObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.Tag;
import com.core.behavior.properties.AmazonIntegrationProperties;
import com.core.behavior.properties.AmazonProperties;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Service
@Data
public class ClientIntegrationAws {

    private AmazonS3 amazonS3;
    private static final String REGION = "us-east-1";

    @Autowired
    private AmazonIntegrationProperties amazonConfiguration;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(amazonConfiguration.getAccessKey(), amazonConfiguration.getSecretKey());
        this.amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(REGION) 
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

    }

    public String uploadFile(File file, String folder, List<Tag> tags) throws IOException {
        PutObjectRequest request = new EncryptedPutObjectRequest(amazonConfiguration.getBucketName(),  folder + "/" + file.getName(), file);        
        ObjectTagging otag = new ObjectTagging(tags);
        request.setTagging(otag);
        PutObjectResult result = this.amazonS3.putObject(request);
        return result.getETag();        
    }
    
   

}
