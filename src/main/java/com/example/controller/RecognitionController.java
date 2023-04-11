package com.example.controller;

import com.example.dto.ImageDto;
import com.example.model.Image;
import com.example.service.abstracts.RecognitionService;
import com.example.util.response.Payload;
import com.example.util.response.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class RecognitionController {
    private RecognitionService recognitionService;

    @PostMapping("/recognition")
    public ResponseEntity<Payload<Image>> recognateImage(@RequestBody ImageDto imageDto) {
        String base64 = imageDto.getBase64();
        byte[] decodedFile = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        try (OutputStream stream = new FileOutputStream(imageDto.getOriginalFilename())) {
            stream.write(decodedFile);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
