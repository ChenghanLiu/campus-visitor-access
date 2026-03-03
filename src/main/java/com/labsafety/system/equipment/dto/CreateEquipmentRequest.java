package com.labsafety.system.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class CreateEquipmentRequest {

    @NotNull(message = "Lab id cannot be null")
    private Long labId;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotBlank(message = "Equipment name cannot be blank")
    private String name;

    private String model;

    private String specification;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    private LocalDate purchaseDate;

    private String storageLocation;

    private String status;

    private String description;

    public CreateEquipmentRequest() {
    }

    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public String getSpecification() {
        return specification;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
