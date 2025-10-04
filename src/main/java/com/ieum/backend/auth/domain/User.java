package com.ieum.backend.auth.domain;

import com.ieum.backend.common.domain.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user")
public class User extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String birth;

    public enum Gender { MALE, FEMALE }

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Gender gender;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public User(String userId, String userName, String password, String email, String birth, Gender gender, UserRole userRole) {
        this.userId   = userId;
        this.userName = userName;
        this.password = password;
        this.email    = email;
        this.birth    = birth;
        this.gender   = gender;
        this.userRole = userRole;
    }
}