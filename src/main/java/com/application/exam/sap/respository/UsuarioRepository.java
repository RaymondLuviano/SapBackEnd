package com.application.exam.sap.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.exam.sap.entity.User;

@Repository
public interface UsuarioRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
}
