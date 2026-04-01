package com.app.myworld.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.myworld.model.Role;
import com.app.myworld.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndIsActiveTrue(String username);
    Optional<User> findByVerificationToken(String token);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findAllByRoleAndIsActiveTrue(Role role);
    Optional<User> findByResetPasswordToken(String token);
}
