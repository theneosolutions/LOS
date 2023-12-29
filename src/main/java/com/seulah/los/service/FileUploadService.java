package com.seulah.los.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class FileUploadService {

    @Value("${application.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public FileUploadService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }


    public byte[] downloadFile(final String fileName) {
        try {
            byte[] content;
            final S3Object s3Object = s3Client.getObject(bucketName, fileName);
            final S3ObjectInputStream stream = s3Object.getObjectContent();
            content = IOUtils.toByteArray(stream);
            s3Object.close();
            return content;
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                log.error("File Not Found");
            }
        } catch (IOException e) {
            log.error("Exception", e);
        }
        return new byte[0];
    }

}
