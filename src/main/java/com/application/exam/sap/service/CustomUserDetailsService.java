package com.application.exam.sap.service;

import com.application.exam.sap.entity.User;
import com.application.exam.sap.respository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;




@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private final UsuarioRepository repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User usuario = repo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Not found"));
		return org.springframework.security.core.userdetails.User.builder()
				.username(usuario.getUsername())
				.password(usuario.getPassword())
				.roles(usuario.getRole()
						.replace("ROLE_", ""))
						.disabled(!usuario.isEnabled()).build();
	}
}