package com.application.exam.sap.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.application.exam.sap.service.CustomUserDetailsService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	
	@Autowired
	private final JwtService jwtService;
	
	@Autowired
	private final CustomUserDetailsService userDetailServie;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		String header = req.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			try {
				Claims claims = jwtService.parse(token);
				String username = claims.getSubject();
				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails ud = userDetailServie.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(ud, null,
							ud.getAuthorities());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			} catch (Exception e) {
// token inválido o expirado: continuar sin autenticar para que caiga en 401
			}
		}
		chain.doFilter(req, res);
	}
}
