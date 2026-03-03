package com.labsafety.system.reservation.dto;

import jakarta.validation.constraints.Size;

public class ReservationDecisionRequest {

    @Size(max = 500)
    private String decisionNote;

    public ReservationDecisionRequest() {
    }

    public String getDecisionNote() {
        return decisionNote;
    }

    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }
}
