package com.seulah.los.feignClient;

import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", url = "http://localhost:8090/api/v1/cms/upload")
public interface FileUploadFeign {
    @RequestLine("POST /fileUpload")
    @Headers("Content-Type: multipart/form-data")
    String uploadFile(@RequestPart(value = "file") MultipartFile file);

}



