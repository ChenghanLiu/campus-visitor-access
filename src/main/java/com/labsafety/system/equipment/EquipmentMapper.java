package com.labsafety.system.equipment;

import com.labsafety.system.equipment.dto.CreateEquipmentRequest;
import com.labsafety.system.equipment.dto.EquipmentResponse;
import com.labsafety.system.equipment.dto.UpdateEquipmentRequest;
import com.labsafety.system.lab.Lab;

public class EquipmentMapper {

    private EquipmentMapper() {
    }

    public static Equipment toEntity(CreateEquipmentRequest request, Lab lab) {
        Equipment equipment = new Equipment();
        equipment.setLab(lab);
        equipment.setCategory(request.getCategory());
        equipment.setName(request.getName());
        equipment.setModel(request.getModel());
        equipment.setSpecification(request.getSpecification());
        equipment.setQuantity(request.getQuantity());
        equipment.setPurchaseDate(request.getPurchaseDate());
        equipment.setStorageLocation(request.getStorageLocation());

        if (request.getStatus() != null) {
            equipment.setStatus(request.getStatus());
        }

        equipment.setDescription(request.getDescription());

        return equipment;
    }

    public static void updateEntity(Equipment equipment, UpdateEquipmentRequest request, Lab lab) {
        equipment.setLab(lab);
        equipment.setCategory(request.getCategory());
        equipment.setName(request.getName());
        equipment.setModel(request.getModel());
        equipment.setSpecification(request.getSpecification());
        equipment.setQuantity(request.getQuantity());
        equipment.setPurchaseDate(request.getPurchaseDate());
        equipment.setStorageLocation(request.getStorageLocation());
        equipment.setStatus(request.getStatus());
        equipment.setDescription(request.getDescription());
    }

    public static EquipmentResponse toResponse(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getLab().getId(),
                equipment.getLab().getName(),
                equipment.getCategory(),
                equipment.getName(),
                equipment.getModel(),
                equipment.getSpecification(),
                equipment.getQuantity(),
                equipment.getPurchaseDate(),
                equipment.getStorageLocation(),
                equipment.getStatus(),
                equipment.getDescription()
        );
    }
}
