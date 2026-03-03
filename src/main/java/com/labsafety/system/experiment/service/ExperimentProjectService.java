package com.labsafety.system.experiment.service;

import com.labsafety.system.experiment.ExperimentProject;
import com.labsafety.system.experiment.ExperimentProjectRepository;
import com.labsafety.system.experiment.dto.ExperimentProjectCreateRequest;
import com.labsafety.system.experiment.dto.ExperimentProjectResponse;
import com.labsafety.system.experiment.dto.ExperimentProjectUpdateRequest;
import com.labsafety.system.experiment.mapper.ExperimentProjectMapper;
import com.labsafety.system.lab.Lab;
import com.labsafety.system.lab.LabRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ExperimentProjectService {

    private final ExperimentProjectRepository experimentProjectRepository;
    private final LabRepository labRepository;

    public ExperimentProjectService(ExperimentProjectRepository experimentProjectRepository,
                                    LabRepository labRepository) {
        this.experimentProjectRepository = experimentProjectRepository;
        this.labRepository = labRepository;
    }

    @Transactional
    public ExperimentProjectResponse create(ExperimentProjectCreateRequest request) {
        validateTimeRange(request.getStartTime(), request.getEndTime());

        Lab lab = labRepository.findById(request.getLabId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Lab not found: " + request.getLabId()));

        ExperimentProject project = ExperimentProjectMapper.toEntity(request, lab);
        ExperimentProject saved = experimentProjectRepository.save(project);
        return ExperimentProjectMapper.toResponse(saved);
    }

    @Transactional
    public ExperimentProjectResponse update(Long id, ExperimentProjectUpdateRequest request) {
        validateTimeRange(request.getStartTime(), request.getEndTime());

        ExperimentProject project = experimentProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Experiment project not found: " + id));

        Lab lab = labRepository.findById(request.getLabId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Lab not found: " + request.getLabId()));

        ExperimentProjectMapper.updateEntity(project, request, lab);
        ExperimentProject saved = experimentProjectRepository.save(project);
        return ExperimentProjectMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        ExperimentProject project = experimentProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Experiment project not found: " + id));
        experimentProjectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public ExperimentProjectResponse getById(Long id) {
        ExperimentProject project = experimentProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Experiment project not found: " + id));
        return ExperimentProjectMapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    public Page<ExperimentProjectResponse> list(Long labId, String keyword, Pageable pageable) {
        boolean hasLabId = (labId != null);
        boolean hasKeyword = StringUtils.hasText(keyword);

        Page<ExperimentProject> page;

        if (hasLabId && hasKeyword) {
            page = experimentProjectRepository.findByLab_IdAndNameContainingIgnoreCase(labId, keyword, pageable);
        } else if (hasLabId) {
            page = experimentProjectRepository.findByLab_Id(labId, pageable);
        } else if (hasKeyword) {
            page = experimentProjectRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            page = experimentProjectRepository.findAll(pageable);
        }

        return page.map(ExperimentProjectMapper::toResponse);
    }

    private void validateTimeRange(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        if (start == null || end == null) {
            throw new ResponseStatusException(BAD_REQUEST, "startTime and endTime must not be null");
        }
        if (!end.isAfter(start)) {
            throw new ResponseStatusException(BAD_REQUEST, "endTime must be after startTime");
        }
    }
}
