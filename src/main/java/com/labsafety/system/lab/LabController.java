package com.labsafety.system.lab;

import com.labsafety.system.lab.dto.CreateLabRequest;
import com.labsafety.system.lab.dto.LabResponse;
import com.labsafety.system.lab.dto.UpdateLabRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labs")
public class LabController {

    private final LabService labService;

    public LabController(LabService labService) {
        this.labService = labService;
    }

    @GetMapping
    public ResponseEntity<List<LabResponse>> list() {
        List<LabResponse> list = labService.listLabs()
                .stream()
                .map(LabMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(LabMapper.toResponse(labService.getLab(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<LabResponse> create(@Valid @RequestBody CreateLabRequest req) {
        Lab created = labService.createLab(req);
        return ResponseEntity.ok(LabMapper.toResponse(created));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<LabResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLabRequest req
    ) {
        Lab updated = labService.updateLab(id, req);
        return ResponseEntity.ok(LabMapper.toResponse(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        labService.deleteLab(id);
        return ResponseEntity.noContent().build();
    }
}
