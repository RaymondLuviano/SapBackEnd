package com.application.exam.sap.config;


import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.application.exam.sap.security.JwtAuthFilter;
import com.application.exam.sap.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomUserDetailsService uds;
	private final JwtAuthFilter jwtAuthFilter;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
		return cfg.getAuthenticationManager();
	}

	@Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		  http
		    .csrf(csrf -> csrf.disable())
		    .cors(cors -> {})
		    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		    .authorizeHttpRequests(auth -> auth
		      .requestMatchers("/api/auth/**").permitAll()
		      //.anyRequest().authenticated());
		  .anyRequest().permitAll());
		  return http.build();
		}

		@Bean CorsConfigurationSource corsConfigurationSource() {
		  var cfg = new CorsConfiguration();
		  cfg.setAllowedOrigins(List.of("http://localhost:4200"));
		  cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
		  cfg.setAllowedHeaders(List.of("*"));
		  var src = new UrlBasedCorsConfigurationSource();
		  src.registerCorsConfiguration("/**", cfg);
		  return src;
		}
}