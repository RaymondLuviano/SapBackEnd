package com.application.exam.sap.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "usuarios", schema = "sap")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuarios {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "username")
    private String username;

	@Column(name = "password")
    private String password;

	@Column(name = "enabled")
    private boolean enabled;

	@Column(name = "role")
    private String role;

}
