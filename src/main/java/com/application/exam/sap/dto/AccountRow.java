package com.application.exam.sap.dto;

import java.math.BigDecimal;

public record AccountRow(Long id, String name, String currency, BigDecimal balance) {
	
	
}