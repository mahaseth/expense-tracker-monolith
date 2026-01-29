package com.expense.tracker.reporting.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.expense.tracker.category.repository.CategoryRepository;
import com.expense.tracker.reporting.dto.CategoryBreakdownRaw;
import com.expense.tracker.reporting.dto.CategoryBreakdownResponse;
import com.expense.tracker.reporting.dto.MonthlyTotalResponse;
import com.expense.tracker.reporting.repository.ReportingRepository;
import com.expense.tracker.reporting.service.ReportingService;
import com.expense.tracker.user.entity.User;
import com.expense.tracker.user.repository.UserRepository;

@Service
public class ReportingServiceImpl implements ReportingService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ReportingRepository reportingRepository;

	@Autowired
	private UserRepository userRepository;

	private User getCurrentUserEntity() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Override
	public List<MonthlyTotalResponse> getMonthlyTotals(int year) {
		User user = getCurrentUserEntity();

		LocalDate start = LocalDate.of(year, 1, 1);
		LocalDate end = LocalDate.of(year, 12, 31);

		return reportingRepository.getMonthlyTotals(user.getId(), start, end);
	}

	public List<CategoryBreakdownResponse> getCategoryBreakdown(LocalDate from, LocalDate to) {
		User user = getCurrentUserEntity();

		if (from == null || to == null) {
			throw new IllegalArgumentException("Both 'from' and 'to' dates are required");
		}
		if (from.isAfter(to)) {
			throw new IllegalArgumentException("'from' date cannot be after 'to' date");
		}

		// 1) Raw totals (categoryId -> total)
		List<CategoryBreakdownRaw> raw = reportingRepository.getCategoryBreakdownRaw(user.getId(), from, to);

		// 2) Fetch category names in one query
		List<String> categoryIds = raw.stream().map(CategoryBreakdownRaw::getCategoryId).toList();
		var categories = categoryRepository.findAllByUserIdAndIdIn(user.getId(), categoryIds);

		// 3) Build lookup map: categoryId -> categoryName
		var nameMap = categories.stream().collect(java.util.stream.Collectors.toMap(c -> c.getId(), c -> c.getName()));

		// 4) Merge into final response (unknown categoryId -> "Unknown")
		return raw.stream().map(r -> new CategoryBreakdownResponse(r.getCategoryId(),
				nameMap.getOrDefault(r.getCategoryId(), "Unknown"), r.getTotal())).toList();
	}
}
