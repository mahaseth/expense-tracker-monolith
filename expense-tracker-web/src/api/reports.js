import { api } from './client'

export async function getMonthlyTotals(year) {
  return api(`/api/reports/monthly?year=${year}`)
}

export async function getCategoryBreakdown(from, to) {
  return api(`/api/reports/category-breakdown?from=${from}&to=${to}`)
}
