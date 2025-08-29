package com.application.exam.sap.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.exam.sap.dto.AccountRow;
import com.application.exam.sap.dto.DashboardSummary;
import com.application.exam.sap.dto.PieSlice;
import com.application.exam.sap.dto.TopExpense;
import com.application.exam.sap.respository.AccountRepository;
import com.application.exam.sap.respository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
	
	@Autowired
	private final TransactionRepository txRepo;
	
	@Autowired
	private final AccountRepository accRepo;

	public List<PieSlice> transfersPie(Long userId) {
		return txRepo.sumTransfersByDestAccount(userId).stream()
				.map(r -> new PieSlice((String) r[0], (BigDecimal) r[1])).toList();
	}

	public List<TopExpense> top3Expenses(Long userId) {
		return txRepo.topExpenses(userId).stream().map(r -> new TopExpense((String) r[0], (BigDecimal) r[1])).limit(3)
				.toList();
	}

	public List<AccountRow> accountBalances(Long userId) {
		var accounts = accRepo.findByUserId(userId);
		var txs = txRepo.findByUserId(userId);
		Map<Long, BigDecimal> delta = new HashMap<>();
		for (var a : accounts)
			delta.put(a.getId(), BigDecimal.ZERO);

		for (var t : txs) {
			switch (t.getType()) {
			case "DEPOSIT" -> {
				if (t.getDestAccount() != null)
					delta.compute(t.getDestAccount().getId(), (k, v) -> v.add(t.getAmount()));
			}
			case "WITHDRAWAL" -> {
				if (t.getSourceAccount() != null)
					delta.compute(t.getSourceAccount().getId(), (k, v) -> v.subtract(t.getAmount()));
			}
			case "TRANSFER" -> {
				if (t.getSourceAccount() != null)
					delta.compute(t.getSourceAccount().getId(), (k, v) -> v.subtract(t.getAmount()));
				if (t.getDestAccount() != null)
					delta.compute(t.getDestAccount().getId(), (k, v) -> v.add(t.getAmount()));
			}
			case "EXPENSE" -> {
				if (t.getSourceAccount() != null)
					delta.compute(t.getSourceAccount().getId(), (k, v) -> v.subtract(t.getAmount()));
			}
			}
		}

		return accounts.stream().map(
				a -> new AccountRow(a.getId(), a.getName(), a.getCurrency(), a.getBalance().add(delta.get(a.getId()))))
				.toList();
	}

	public DashboardSummary summary(Long userId) {
		return new DashboardSummary(transfersPie(userId), top3Expenses(userId), accountBalances(userId));
	}
}