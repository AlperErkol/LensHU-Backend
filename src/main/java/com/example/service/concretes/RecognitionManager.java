package com.example.service.concretes;

import com.example.model.Image;
import com.example.service.abstracts.RecognitionService;
import com.example.util.response.ResponseModel;

public class RecognitionManager implements RecognitionService {
    @Override
    public ResponseModel<Image> recognateImage(Image image) {
        System.out.print(image);
        return null;
    }
}
