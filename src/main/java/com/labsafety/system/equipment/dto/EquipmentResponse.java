package com.labsafety.system.equipment.dto;

import java.time.LocalDate;

public class EquipmentResponse {

    private Long id;

    private Long labId;

    private String labName;

    private String category;

    private String name;

    private String model;

    private String specification;

    private Integer quantity;

    private LocalDate purchaseDate;

    private String storageLocation;

    private String status;

    private String description;

    public EquipmentResponse() {
    }

    public EquipmentResponse(Long id,
                             Long labId,
                             String labName,
                             String category,
                             String name,
                             String model,
                             String specification,
                             Integer quantity,
                             LocalDate purchaseDate,
                             String storageLocation,
                             String status,
                             String description) {
        this.id = id;
        this.labId = labId;
        this.labName = labName;
        this.category = category;
        this.name = name;
        this.model = model;
        this.specification = specification;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.storageLocation = storageLocation;
        this.status = status;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Long getLabId() {
        return labId;
    }

    public String getLabName() {
        return labName;
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
}
