package com.application.exam.sap.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.exam.sap.dto.AccountRow;
import com.application.exam.sap.dto.DashboardSummary;
import com.application.exam.sap.dto.PieSlice;
import com.application.exam.sap.dto.TopExpense;
import com.application.exam.sap.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

	@Autowired
	private final DashboardService svc;

	//Devuelve el primer registro solo como ejemplo
	private Long currentUserId(Authentication auth) {
		return 1L;
	}

	@GetMapping("/summary")
	public DashboardSummary summary(Authentication auth) {
		return svc.summary(currentUserId(auth));
	}

	@GetMapping("/transfers-pie")
	public List<PieSlice> transfersPie(Authentication auth) {
		return svc.transfersPie(currentUserId(auth));
	}

	@GetMapping("/top-expenses")
	public List<TopExpense> topExpenses(Authentication auth) {
		return svc.top3Expenses(currentUserId(auth));
	}

	@GetMapping("/account-balances")
	public List<AccountRow> accountBalances(Authentication auth) {
		return svc.accountBalances(currentUserId(auth));
	}
}
