package com.labsafety.system.lab;

import com.labsafety.system.lab.dto.CreateLabRequest;
import com.labsafety.system.lab.dto.LabResponse;
import com.labsafety.system.lab.dto.UpdateLabRequest;

public final class LabMapper {

    private LabMapper() {
    }

    public static LabResponse toResponse(Lab lab) {
        LabResponse resp = new LabResponse();
        resp.setId(lab.getId());
        resp.setName(lab.getName());
        resp.setType(lab.getType());
        resp.setLocation(lab.getLocation());
        resp.setCapacity(lab.getCapacity());
        resp.setOpenTime(lab.getOpenTime());
        resp.setCloseTime(lab.getCloseTime());
        resp.setManagerName(lab.getManagerName());
        resp.setManagerPhone(lab.getManagerPhone());
        resp.setManagerEmail(lab.getManagerEmail());
        resp.setDetails(lab.getDetails());
        return resp;
    }

    public static Lab fromCreate(CreateLabRequest req) {
        Lab lab = new Lab();
        lab.setName(req.getName());
        lab.setType(req.getType());
        lab.setLocation(req.getLocation());
        lab.setCapacity(req.getCapacity());
        lab.setOpenTime(req.getOpenTime());
        lab.setCloseTime(req.getCloseTime());
        lab.setManagerName(req.getManagerName());
        lab.setManagerPhone(req.getManagerPhone());
        lab.setManagerEmail(req.getManagerEmail());
        lab.setDetails(req.getDetails());
        return lab;
    }

    public static void applyUpdate(Lab lab, UpdateLabRequest req) {
        lab.setName(req.getName());
        lab.setType(req.getType());
        lab.setLocation(req.getLocation());
        lab.setCapacity(req.getCapacity());
        lab.setOpenTime(req.getOpenTime());
        lab.setCloseTime(req.getCloseTime());
        lab.setManagerName(req.getManagerName());
        lab.setManagerPhone(req.getManagerPhone());
        lab.setManagerEmail(req.getManagerEmail());
        lab.setDetails(req.getDetails());
    }
}
