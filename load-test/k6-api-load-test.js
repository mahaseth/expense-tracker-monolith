/**
 * k6 API Load Test for Expense Tracker
 *
 * Prerequisites:
 * - Install k6: https://k6.io/docs/getting-started/installation/
 * - Start the expense-tracker application (default: http://localhost:8080)
 *
 * Run:
 *   k6 run load-test/k6-api-load-test.js
 *
 * With custom base URL:
 *   k6 run -e BASE_URL=http://localhost:8080 load-test/k6-api-load-test.js
 *
 * With more VUs and duration:
 *   k6 run --vus 20 --duration 60s load-test/k6-api-load-test.js
 */

import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  stages: [
    { duration: '30s', target: 5 },   // Ramp up to 5 users
    { duration: '1m', target: 10 },   // Stay at 10 users
    { duration: '30s', target: 20 },   // Spike to 20 users
    { duration: '1m', target: 10 },    // Scale down to 10 users
    { duration: '30s', target: 0 },    // Ramp down to 0
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'],  // 95% of requests should be below 2s
    http_req_failed: ['rate<0.05'],     // Error rate should be below 5%
  },
};

export function setup() {
  // Register a unique user for load testing
  const email = `loadtest-${Date.now()}-${__VU}@example.com`;
  const registerPayload = JSON.stringify({
    fullName: 'Load Test User',
    email: email,
    password: 'loadtest123',
  });

  const registerRes = http.post(`${BASE_URL}/api/auth/register`, registerPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  if (!check(registerRes, { 'register success': (r) => r.status === 200 })) {
    console.error(`Register failed: ${registerRes.status} - ${registerRes.body}`);
    return null;
  }

  const authResponse = registerRes.json();
  const token = authResponse.token;
  return { token, email };
}

export default function (data) {
  if (!data || !data.token) {
    sleep(1);
    return;
  }

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${data.token}`,
  };

  // 1. Get current user
  const meRes = http.get(`${BASE_URL}/api/users/me`, { headers });
  check(meRes, { 'GET /api/users/me': (r) => r.status === 200 });

  sleep(0.5);

  // 2. Get categories (may be empty initially)
  const categoriesRes = http.get(`${BASE_URL}/api/categories`, { headers });
  check(categoriesRes, { 'GET /api/categories': (r) => r.status === 200 });

  const categories = categoriesRes.json() || [];
  let categoryId = null;

  if (categories.length > 0) {
    categoryId = categories[0].id;
  } else {
    // Create a category
    const createCategoryPayload = JSON.stringify({ name: `Category-${__VU}-${__ITER}` });
    const createCatRes = http.post(`${BASE_URL}/api/categories`, createCategoryPayload, { headers });
    if (check(createCatRes, { 'POST /api/categories': (r) => r.status === 200 })) {
      const cat = createCatRes.json();
      categoryId = cat.id;
    }
  }

  sleep(0.5);

  // 3. Create expense (if we have a category)
  if (categoryId) {
    const expensePayload = JSON.stringify({
      title: `Expense-${__VU}-${__ITER}`,
      amount: Math.random() * 100 + 10,
      categoryId: categoryId,
      expenseDate: new Date().toISOString().split('T')[0],
      notes: 'Load test expense',
    });
    const createExpenseRes = http.post(`${BASE_URL}/api/expenses`, expensePayload, { headers });
    check(createExpenseRes, { 'POST /api/expenses': (r) => r.status === 200 });
  }

  sleep(0.5);

  // 4. Get expenses
  const expensesRes = http.get(`${BASE_URL}/api/expenses`, { headers });
  check(expensesRes, { 'GET /api/expenses': (r) => r.status === 200 });

  sleep(0.5);

  // 5. Get monthly report
  const year = new Date().getFullYear();
  const monthlyRes = http.get(`${BASE_URL}/api/reports/monthly?year=${year}`, { headers });
  check(monthlyRes, { 'GET /api/reports/monthly': (r) => r.status === 200 });

  sleep(1);
}

export function teardown(data) {
  // Optional: cleanup or summary
}
