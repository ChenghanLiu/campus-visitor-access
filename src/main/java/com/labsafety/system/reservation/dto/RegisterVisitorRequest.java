package com.labsafety.system.reservation.dto;

import jakarta.validation.constraints.Size;

public class RegisterVisitorRequest {

    @Size(max = 500)
    private String idCardPhotoUrl;

    @Size(max = 500)
    private String facePhotoUrl;

    @Size(max = 500)
    private String verifyNote;

    public String getIdCardPhotoUrl() { return idCardPhotoUrl; }
    public void setIdCardPhotoUrl(String idCardPhotoUrl) { this.idCardPhotoUrl = idCardPhotoUrl; }

    public String getFacePhotoUrl() { return facePhotoUrl; }
    public void setFacePhotoUrl(String facePhotoUrl) { this.facePhotoUrl = facePhotoUrl; }

    public String getVerifyNote() { return verifyNote; }
    public void setVerifyNote(String verifyNote) { this.verifyNote = verifyNote; }
}