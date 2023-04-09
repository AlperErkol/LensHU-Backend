package com.example.controller;

import com.example.model.Image;
import com.example.service.abstracts.RecognitionService;
import com.example.util.response.Payload;
import com.example.util.response.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class RecognitionController {
    private RecognitionService recognitionService;

    @PostMapping("/recognition")
    public ResponseEntity<Payload<Image>> recognateImage(@RequestParam Image image) {
        ResponseModel<Image> responseModel = this.recognitionService.recognateImage(image);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }
}
