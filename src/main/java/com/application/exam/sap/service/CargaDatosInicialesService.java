package com.application.exam.sap.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.application.exam.sap.entity.User;
import com.application.exam.sap.respository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component @RequiredArgsConstructor
public class CargaDatosInicialesService implements ApplicationRunner {
  private final UsuarioRepository repo;
  private final PasswordEncoder encoder;

  @Override public void run(ApplicationArguments args) {
    if (repo.findByUsername("raymundo@gmail.com").isEmpty()) {
      repo.save(User.builder()
        .username("raymundo@gmail.com")
        .password(encoder.encode("123456789")) // prueba
        .role("ROLE_ADMIN").enabled(true).build());
    }
  }
}