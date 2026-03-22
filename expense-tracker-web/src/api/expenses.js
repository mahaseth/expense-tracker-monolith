import { api } from './client'

function params(opts = {}) {
  const p = new URLSearchParams()
  if (opts.from) p.set('from', opts.from)
  if (opts.to) p.set('to', opts.to)
  if (opts.categoryId) p.set('categoryId', opts.categoryId)
  const q = p.toString()
  return q ? `?${q}` : ''
}

export async function getExpenses(filters = {}) {
  return api(`/api/expenses${params(filters)}`)
}

export async function getExpense(id) {
  return api(`/api/expenses/${id}`)
}

export async function createExpense(data) {
  return api('/api/expenses', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export async function updateExpense(id, data) {
  return api(`/api/expenses/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export async function deleteExpense(id) {
  return api(`/api/expenses/${id}`, { method: 'DELETE' })
}
