package com.lassriver.bookworm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lassriver.bookworm.entities.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Necesitamos esto para validar si el correo ya existe (AC-1-5)
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select u from User u where upper(u.role) in :roles")
    List<User> findAllByRoleUpperIn(@Param("roles") Collection<String> roles);
}
