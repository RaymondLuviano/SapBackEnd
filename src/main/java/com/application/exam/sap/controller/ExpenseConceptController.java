package com.application.exam.sap.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.exam.sap.entity.ExpenseConcept;
import com.application.exam.sap.entity.User;
import com.application.exam.sap.respository.ExpenseConceptRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/concepts")
@RequiredArgsConstructor
public class ExpenseConceptController {
	
	@Autowired
	private final ExpenseConceptRepository repo;

	private Long currentUserId(Authentication auth) {
		return 1L;
	}

	@PostMapping
	public ExpenseConcept create(@RequestBody Map<String, String> body, Authentication auth) {
		Long userId = currentUserId(auth);
		String name = body.get("name");
		return repo.findByUser_IdAndName(userId, name).orElseGet(() -> {
			ExpenseConcept c = new ExpenseConcept();
			var u = new User();
			u.setId(userId);
			c.setUser(u);
			c.setName(name);
			return repo.save(c);
		});
	}

	@GetMapping
	public List<ExpenseConcept> list(Authentication auth) {
		return repo.findByUserId(currentUserId(auth));
	}
}
