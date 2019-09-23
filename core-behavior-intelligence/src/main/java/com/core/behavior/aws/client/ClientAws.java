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
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.core.behavior.properties.AmazonProperties;
import java.io.File;
import java.io.IOException;
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
public class ClientAws {

    private AmazonS3 amazonS3;
    private static final String REGION = "sa-east-1";
    private static final String FOLDER_RETURN = "/RETORNO/";

    @Autowired
    private AmazonProperties amazonConfiguration;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(amazonConfiguration.getAccessKey(), amazonConfiguration.getSecretKey());
        this.amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(REGION) 
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

    }

    public String uploadFile(File file, String folder) throws IOException {
        PutObjectRequest request = new EncryptedPutObjectRequest(amazonConfiguration.getBucketName(),  folder + "/" + file.getName(), file);        
        PutObjectResult result = this.amazonS3.putObject(request);
        return result.getETag();        
    }
    
    public File downloadFile(String fileName,String folder) throws IOException{
        GetObjectRequest request = new GetObjectRequest(amazonConfiguration.getBucketName(), folder + "/" + fileName);
        File fileTemp = File.createTempFile("down", "load");
        this.amazonS3.getObject(request, fileTemp);
        
        return fileTemp;
    }
    
    public File downloadFileReturn(String fileName,String folder) throws IOException{
        GetObjectRequest request = new GetObjectRequest(amazonConfiguration.getBucketName(), folder + FOLDER_RETURN + fileName);
        File fileTemp = File.createTempFile("down", "load");
        this.amazonS3.getObject(request, fileTemp);
        
        return fileTemp;
    }
    
    public String uploadFileReturn(File file, String folder) throws IOException {
        PutObjectRequest request = new EncryptedPutObjectRequest(amazonConfiguration.getBucketName(),  folder + FOLDER_RETURN + file.getName(), file);        
        PutObjectResult result = this.amazonS3.putObject(request);        
        return result.getETag();        
    }

}
