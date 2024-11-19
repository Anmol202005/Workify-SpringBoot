package com.workify.auth.Controller;

import com.workify.auth.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("/api/S3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // Endpoint to upload files with validation for formats
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("type") String type) {
        try {
            // Validate file format based on type
            String contentType = file.getContentType();
            if (type.equalsIgnoreCase("profile-pic") && (contentType == null || !contentType.equals("image/png"))) {
                return ResponseEntity.badRequest().body("Invalid file format for profile picture. Only PNG is allowed.");
            }

            if (type.equalsIgnoreCase("certificate") && (contentType == null || !contentType.equals("application/pdf"))) {
                return ResponseEntity.badRequest().body("Invalid file format for certificate. Only PDF is allowed.");
            }

            // Determine the folder name based on the type
            String folderName = (type.equalsIgnoreCase("profile-pic")) ? "profile-pictures" : "certificates";

            // Upload file to S3 and get the file key
            String fileKey = s3Service.uploadFile(file, folderName);

            return ResponseEntity.ok("File uploaded successfully: " + fileKey);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }


    @GetMapping("/download")
    public ResponseEntity<String> downloadFile(@RequestParam("fileKey") String fileKey) {
        try {
            // Generate pre-signed URL for downloading
            URL preSignedUrl = s3Service.generateDownloadUrl(fileKey);
            return ResponseEntity.ok("Download URL: " + preSignedUrl.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating pre-signed URL: " + e.getMessage());
        }
    }
    @GetMapping("/view")
    public ResponseEntity<String> viewFile(@RequestParam("fileKey") String fileKey) {
        try {

            URL preSignedUrl = s3Service.generateViewPresignedUrl(fileKey);
            return ResponseEntity.ok("View URL: " + preSignedUrl.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating pre-signed URL: " + e.getMessage());
        }
    }
}

