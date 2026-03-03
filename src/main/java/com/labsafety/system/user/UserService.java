package com.labsafety.system.user;

import com.labsafety.system.reservation.ReservationRepository;
import com.labsafety.system.user.dto.CreateUserRequest;
import com.labsafety.system.user.dto.UpdateMyProfileRequest;
import com.labsafety.system.user.dto.UpdateUserRequest;
import com.labsafety.system.user.dto.UserResponse;
import com.labsafety.system.user.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservationRepository reservationRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.reservationRepository = reservationRepository;
    }
    // ===============================
    // ADMIN: create user
    // ===============================
    public UserResponse createByAdmin(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role role = parseRoleOrThrow(request.getRole());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        User saved = userRepository.save(user);
        return UserMapper.toResponse(saved);
    }

    // ===============================
    // ADMIN: list users
    // ===============================
    @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toResponse);
    }

    // ===============================
    // ADMIN: get user by id
    // ===============================
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toResponse(user);
    }

    // ===============================
    // ADMIN: update user (email/role/password)
    // ===============================
    public UserResponse updateByAdmin(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            Optional<User> emailOwner = userRepository.findByEmail(request.getEmail());
            if (emailOwner.isPresent() && !emailOwner.get().getId().equals(user.getId())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            user.setRole(parseRoleOrThrow(request.getRole()));
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);
        return UserMapper.toResponse(saved);
    }

    // ===============================
    // ADMIN: delete user
    // ===============================
    @Transactional
    public void deleteByAdmin(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        // 1️⃣ 先删除所有该用户的预约记录（避免外键拦截）
        reservationRepository.deleteAllByStudentId(id);

        // 2️⃣ 再删除用户
        userRepository.deleteById(id);
    }

    // ===============================
    // ALL: get my profile
    // ===============================
    @Transactional(readOnly = true)
    public UserResponse getMe(String username) {
        User me = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toResponse(me);
    }

    // ===============================
    // ALL: update my profile (email/password only)
    // ===============================
    public UserResponse updateMe(String username, UpdateMyProfileRequest request) {
        User me = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            Optional<User> emailOwner = userRepository.findByEmail(request.getEmail());
            if (emailOwner.isPresent() && !emailOwner.get().getId().equals(me.getId())) {
                throw new RuntimeException("Email already exists");
            }
            me.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            me.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(me);
        return UserMapper.toResponse(saved);
    }

    private Role parseRoleOrThrow(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new RuntimeException("Role is required");
        }
        try {
            return Role.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid role");
        }
    }
}
