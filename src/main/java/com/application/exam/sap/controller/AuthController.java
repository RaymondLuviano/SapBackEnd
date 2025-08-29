package com.application.exam.sap.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.application.exam.sap.respository.UsuarioRepository;
import com.application.exam.sap.security.JwtService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthenticationManager authManager;
	
	@Autowired
	private final JwtService jwtService;
	
	

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
		Authentication auth = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
		org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth
				.getPrincipal();
		String role = principal.getAuthorities().iterator().next().getAuthority();
		String token = jwtService.generateToken(principal.getUsername(), role);
		return ResponseEntity.ok(new AuthResponse(token));
	}

	@GetMapping("/me")
	public ResponseEntity<Map<String, Object>> me(Authentication auth) {
		return ResponseEntity.ok(Map.of("username", auth.getName(), "authorities", auth.getAuthorities()));
	}

	@GetMapping("/secure-hello")
	public String secureHello() {
		return "Hola, estás autenticado.";
	}
}

record LoginRequest(String username, String password) {
}

record AuthResponse(String accessToken) {
}
