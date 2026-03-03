package com.labsafety.system.user.mapper;

import com.labsafety.system.user.User;
import com.labsafety.system.user.dto.UserResponse;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setUsername(user.getUsername());
        r.setEmail(user.getEmail());
        r.setRole(user.getRole() == null ? null : user.getRole().name());
        return r;
    }
}
