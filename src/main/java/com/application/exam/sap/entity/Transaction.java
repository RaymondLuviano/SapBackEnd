package com.application.exam.sap.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions", schema = "sap")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source_account_id")
	private Account sourceAccount; // puede ser null
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dest_account_id")
	private Account destAccount; // puede ser null
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concept_id")
	private ExpenseConcept concept; // puede ser null
	@Column(nullable = false)
	private String type; // 'DEPOSIT','WITHDRAWAL','TRANSFER','EXPENSE'
	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;
	@Column(nullable = false, length = 3)
	private String currency;
	@Column(name = "occurred_at", nullable = false)
	private OffsetDateTime occurredAt; // map a TIMESTAMPTZ
	private String description;
}