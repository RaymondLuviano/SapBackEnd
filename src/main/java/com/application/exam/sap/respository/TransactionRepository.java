package com.application.exam.sap.respository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.exam.sap.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	@Query("select t.destAccount.name as label, sum(t.amount) as total "
			+ "from Transaction t where t.user.id = :userId and t.type = 'TRANSFER' and t.destAccount is not null "
			+ "group by t.destAccount.name")
	List<Object[]> sumTransfersByDestAccount(@Param("userId") Long userId);

	@Query("select t.concept.name as concept, sum(t.amount) as total "
			+ "from Transaction t where t.user.id = :userId and t.type = 'EXPENSE' and t.concept is not null "
			+ "group by t.concept.name order by sum(t.amount) desc")
	List<Object[]> topExpenses(@Param("userId") Long userId);

	List<Transaction> findByUserId(Long userId);

}
