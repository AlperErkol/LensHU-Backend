package com.example.model;

import lombok.Data;

@Data
public class PasswordModel {
    private String newPassword;
    private String verifyNewPassword;
    public boolean checkIfPasswordsMatch()
    {
        return this.newPassword.equals(this.verifyNewPassword);
    }
}
