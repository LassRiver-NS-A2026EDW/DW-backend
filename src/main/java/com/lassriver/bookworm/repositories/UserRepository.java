package com.lassriver.bookworm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lassriver.bookworm.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Necesitamos esto para validar si el correo ya existe (AC-1-5)
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}