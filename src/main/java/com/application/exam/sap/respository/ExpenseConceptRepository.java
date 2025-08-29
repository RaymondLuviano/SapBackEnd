package com.application.exam.sap.respository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.exam.sap.entity.ExpenseConcept;

@Repository
public interface ExpenseConceptRepository extends JpaRepository<ExpenseConcept, Long> {
List<ExpenseConcept> findByUserId(Long userId);
Optional<ExpenseConcept> findByUser_IdAndName(Long userId, String name);
}