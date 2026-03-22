import { api } from './client'

export async function getCategories() {
  return api('/api/categories')
}

export async function createCategory(name) {
  return api('/api/categories', {
    method: 'POST',
    body: JSON.stringify({ name }),
  })
}

export async function deleteCategory(id) {
  return api(`/api/categories/${id}`, { method: 'DELETE' })
}
