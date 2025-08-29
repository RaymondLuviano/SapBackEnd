package com.application.exam.sap.dto;

import java.util.List;

public record DashboardSummary(List<PieSlice> transfersPie, List<TopExpense> top3Expenses, List<AccountRow> accountBalances) {
	
	
	
}