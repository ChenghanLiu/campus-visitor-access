package com.labsafety.system.experiment.mapper;

import com.labsafety.system.experiment.ExperimentProject;
import com.labsafety.system.experiment.dto.ExperimentProjectCreateRequest;
import com.labsafety.system.experiment.dto.ExperimentProjectResponse;
import com.labsafety.system.experiment.dto.ExperimentProjectUpdateRequest;
import com.labsafety.system.lab.Lab;

public class ExperimentProjectMapper {

    private ExperimentProjectMapper() {
    }

    public static ExperimentProject toEntity(ExperimentProjectCreateRequest request, Lab lab) {
        ExperimentProject project = new ExperimentProject();
        project.setLab(lab);
        project.setName(request.getName());
        project.setObjective(request.getObjective());
        project.setContent(request.getContent());
        project.setRequirements(request.getRequirements());
        project.setStartTime(request.getStartTime());
        project.setEndTime(request.getEndTime());
        return project;
    }

    public static void updateEntity(ExperimentProject project, ExperimentProjectUpdateRequest request, Lab lab) {
        project.setLab(lab);
        project.setName(request.getName());
        project.setObjective(request.getObjective());
        project.setContent(request.getContent());
        project.setRequirements(request.getRequirements());
        project.setStartTime(request.getStartTime());
        project.setEndTime(request.getEndTime());
    }

    public static ExperimentProjectResponse toResponse(ExperimentProject project) {
        ExperimentProjectResponse response = new ExperimentProjectResponse();
        response.setId(project.getId());

        if (project.getLab() != null) {
            response.setLabId(project.getLab().getId());
            response.setLabName(project.getLab().getName());
        }

        response.setName(project.getName());
        response.setObjective(project.getObjective());
        response.setContent(project.getContent());
        response.setRequirements(project.getRequirements());
        response.setStartTime(project.getStartTime());
        response.setEndTime(project.getEndTime());
        response.setCreatedAt(project.getCreatedAt());

        return response;
    }
}
