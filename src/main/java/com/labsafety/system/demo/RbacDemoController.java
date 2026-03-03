package com.labsafety.system.demo;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RbacDemoController {

    @GetMapping("/admin/ping")
    public Map<String, String> adminPing() {
        return Map.of("message", "admin-ok");
    }

    @GetMapping("/teacher/ping")
    public Map<String, String> teacherPing() {
        return Map.of("message", "teacher-ok");
    }

    @GetMapping("/student/ping")
    public Map<String, String> studentPing() {
        return Map.of("message", "student-ok");
    }
}
