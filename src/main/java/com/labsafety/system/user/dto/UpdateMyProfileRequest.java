package com.labsafety.system.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateMyProfileRequest {

    @Email
    @Size(max = 150)
    private String email;

    @Size(min = 6, max = 100)
    private String password;

    public UpdateMyProfileRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
