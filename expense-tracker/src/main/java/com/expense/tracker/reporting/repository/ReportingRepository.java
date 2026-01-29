package com.expense.tracker.reporting.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.expense.tracker.expense.entity.Expense;
import com.expense.tracker.reporting.dto.CategoryBreakdownRaw;
import com.expense.tracker.reporting.dto.CategoryBreakdownResponse;
import com.expense.tracker.reporting.dto.MonthlyTotalResponse;

public interface ReportingRepository extends JpaRepository<Expense, String> {

	// Monthly totals for a specific year (user-scoped)
	@Query("""
			    SELECT new com.expense.tracker.reporting.dto.MonthlyTotalResponse(
			        MONTH(e.expenseDate),
			        COALESCE(SUM(e.amount), 0)
			    )
			    FROM Expense e
			    WHERE e.userId = :userId
			      AND e.expenseDate >= :start
			      AND e.expenseDate <= :end
			    GROUP BY MONTH(e.expenseDate)
			    ORDER BY MONTH(e.expenseDate)
			""")
	List<MonthlyTotalResponse> getMonthlyTotals(@Param("userId") String userId, @Param("start") LocalDate start,
			@Param("end") LocalDate end);

	// Category breakdown for a date range (user-scoped) - RAW (id + total)
	@Query("""
			    SELECT new com.expense.tracker.reporting.dto.CategoryBreakdownRaw(
			        e.categoryId,
			        COALESCE(SUM(e.amount), 0)
			    )
			    FROM Expense e
			    WHERE e.userId = :userId
			      AND e.expenseDate >= :start
			      AND e.expenseDate <= :end
			    GROUP BY e.categoryId
			    ORDER BY COALESCE(SUM(e.amount), 0) DESC
			""")
	List<CategoryBreakdownRaw> getCategoryBreakdownRaw(@Param("userId") String userId, @Param("start") LocalDate start,
			@Param("end") LocalDate end);
}
