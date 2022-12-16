package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class registerUserDto {
    private String name;
    private String surname;
    private String email;
    private String password;
    private String confirmPassword;

    private boolean isPasswordMatch(){
        return password.equals(confirmPassword);
    }


}
