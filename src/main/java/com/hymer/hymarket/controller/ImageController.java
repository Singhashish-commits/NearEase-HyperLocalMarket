package com.hymer.hymarket.controller;

import com.cloudinary.Cloudinary;
import com.hymer.hymarket.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private FileUploadService fileUploadService;
    @Autowired
    public void setFileUploadService(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile( @RequestParam("file") MultipartFile file){
        try{
            String imageUrl=fileUploadService.uploadFile(file);
            return ResponseEntity.ok(Map.of("url", imageUrl));
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Failed to upload file : "+ e.getMessage());
        }
    }
}
