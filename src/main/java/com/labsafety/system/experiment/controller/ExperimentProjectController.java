package com.labsafety.system.experiment.controller;

import com.labsafety.system.experiment.dto.ExperimentProjectCreateRequest;
import com.labsafety.system.experiment.dto.ExperimentProjectResponse;
import com.labsafety.system.experiment.dto.ExperimentProjectUpdateRequest;
import com.labsafety.system.experiment.service.ExperimentProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/experiment-projects")
public class ExperimentProjectController {

    private final ExperimentProjectService experimentProjectService;

    public ExperimentProjectController(ExperimentProjectService experimentProjectService) {
        this.experimentProjectService = experimentProjectService;
    }

    /**
     * Teacher/Admin creates an experiment project.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ExperimentProjectResponse create(@Valid @RequestBody ExperimentProjectCreateRequest request) {
        return experimentProjectService.create(request);
    }

    /**
     * Teacher/Admin updates an experiment project.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ExperimentProjectResponse update(@PathVariable Long id,
                                            @Valid @RequestBody ExperimentProjectUpdateRequest request) {
        return experimentProjectService.update(id, request);
    }

    /**
     * Teacher/Admin deletes an experiment project.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public void delete(@PathVariable Long id) {
        experimentProjectService.delete(id);
    }

    /**
     * Students can view a single experiment project.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    public ExperimentProjectResponse getById(@PathVariable Long id) {
        return experimentProjectService.getById(id);
    }

    /**
     * Students can browse experiment projects with pagination.
     * Optional filters:
     * - labId: filter by lab
     * - keyword: search by name (contains, ignore case)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
    public Page<ExperimentProjectResponse> list(@RequestParam(required = false) Long labId,
                                                @RequestParam(required = false) String keyword,
                                                @PageableDefault(size = 10) Pageable pageable) {
        return experimentProjectService.list(labId, keyword, pageable);
    }
}
