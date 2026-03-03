package com.labsafety.system.equipment;

import org.springframework.data.jpa.domain.Specification;

public class EquipmentSpecs {

    public static Specification<Equipment> categoryEquals(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("category"), category);
        };
    }

    public static Specification<Equipment> labEquals(Long labId) {
        return (root, query, cb) -> {
            if (labId == null) return cb.conjunction();
            return cb.equal(root.get("lab").get("id"), labId);
        };
    }

    public static Specification<Equipment> statusEquals(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Equipment> keywordLike(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String kw = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), kw),
                    cb.like(cb.lower(root.get("model")), kw),
                    cb.like(cb.lower(root.get("specification")), kw),
                    cb.like(cb.lower(root.get("description")), kw)
            );
        };
    }
}
