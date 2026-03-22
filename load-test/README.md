# Expense Tracker - k6 API Load Testing

This directory contains k6 scripts for load testing the Expense Tracker API.

## Prerequisites

Install k6:

- **macOS (Homebrew):** `brew install k6`
- **Linux:** See [k6 installation guide](https://k6.io/docs/getting-started/installation/)
- **Windows:** `choco install k6`

## Running the Application

Start the expense-tracker Spring Boot application before running load tests:

```bash
cd expense-tracker && mvn spring-boot:run
```

Default base URL: `http://localhost:8080`

## Run Load Test

```bash
# Basic run (from project root)
k6 run load-test/k6-api-load-test.js

# Custom base URL
k6 run -e BASE_URL=http://localhost:8080 load-test/k6-api-load-test.js

# Custom load profile (20 VUs for 2 minutes)
k6 run --vus 20 --duration 2m load-test/k6-api-load-test.js

# Smoke test (1 VU, 30 seconds)
k6 run --vus 1 --duration 30s load-test/k6-api-load-test.js
```

## Test Scenarios

The load test covers:

1. **Auth** - Register a new user per VU
2. **User** - GET /api/users/me
3. **Categories** - GET/POST /api/categories
4. **Expenses** - POST/GET /api/expenses
5. **Reports** - GET /api/reports/monthly

## Thresholds

- 95% of requests should complete in under 2 seconds
- Error rate should be below 5%

Customize thresholds in the script's `options.thresholds` if needed.
