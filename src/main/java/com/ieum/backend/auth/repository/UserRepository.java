package com.ieum.backend.auth.repository;

import com.ieum.backend.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인 아이디로 조회/중복검사
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);

    // 이메일 중복검사/조회
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
