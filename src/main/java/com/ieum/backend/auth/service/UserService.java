package com.ieum.backend.auth.service;

import com.ieum.backend.auth.domain.User;
import com.ieum.backend.auth.domain.UserRole;
import com.ieum.backend.auth.dto.SignUpRequestDto;
import com.ieum.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(SignUpRequestDto dto) {

        // 1) 중복 검사 (userId로 변경!)
        if (userRepository.existsByUserId(dto.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }

        // 2) gender 문자열 -> enum 변환 (nullable 허용)
        User.Gender gender = null;
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            String g = dto.getGender().trim().toUpperCase();
            switch (g) {
                case "MALE":
                case "M":
                    gender = User.Gender.MALE; break;
                case "FEMALE":
                case "F":
                    gender = User.Gender.FEMALE; break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "gender 값이 올바르지 않습니다.");
            }
        }

        // 3) 엔티티 생성 (엔티티 생성자 시그니처와 정확히 일치)
        User user = new User(
                dto.getUserId(),
                dto.getUserName(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getEmail(),
                dto.getBirth(),
                gender,
                UserRole.ROLE_USER
        );

        userRepository.save(user);
    }
}
