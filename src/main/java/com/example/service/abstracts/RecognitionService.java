package com.example.service.abstracts;

import com.example.dto.RegisterUserDto;
import com.example.dto.UserDto;
import com.example.model.Image;
import com.example.util.response.ResponseModel;

public interface RecognitionService {
    ResponseModel<Image> recognateImage(Image image);
}
