package com.digitaltravel.erp.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/public/upload")
@CrossOrigin("*")
public class UploadController {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public UploadController() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @PostMapping
    // TODO - Upload tệp đính kèm nghiệp vụ
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            byte[] fileContent = file.getBytes();
            String mimeType = file.getContentType();
            if (mimeType == null)
                mimeType = "image/png";

            String base64 = java.util.Base64.getEncoder().encodeToString(fileContent);
            String dataUri = "data:" + mimeType + ";base64," + base64;

            Map<String, String> response = new HashMap<>();
            response.put("url", dataUri);
            return ResponseEntity.ok(response);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @GetMapping("/files/{fileName:.+}")
    // TODO - Tải tệp đính kèm nghiệp vụ
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                String contentType = null;
                try {
                    contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                } catch (IOException ex) {
                    // Ignore
                }
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
