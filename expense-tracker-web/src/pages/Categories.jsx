import { useState, useEffect } from 'react'
import { FiTrash2 } from 'react-icons/fi'
import * as categoriesApi from '../api/categories'

export default function Categories() {
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [name, setName] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const load = async () => {
    setLoading(true)
    setError('')
    try {
      const list = await categoriesApi.getCategories()
      setCategories(list)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    const trimmed = name.trim()
    if (!trimmed) return
    setSubmitting(true)
    setError('')
    try {
      await categoriesApi.createCategory(trimmed)
      setName('')
      await load()
    } catch (err) {
      setError(err?.message || 'Failed to add category')
    } finally {
      setSubmitting(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this category? Expenses in it will keep the category id.')) return
    try {
      await categoriesApi.deleteCategory(id)
      setCategories((prev) => prev.filter((c) => c.id !== id))
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-slate-800">Categories</h1>

      <form
        onSubmit={handleSubmit}
        className="mb-6 flex flex-wrap items-end gap-3 rounded-xl border border-slate-200 bg-white px-5 py-4 shadow-sm"
      >
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Category name"
          minLength={2}
          maxLength={80}
          className="min-w-[140px] flex-1 rounded-lg border-slate-300"
        />
        <button
          type="submit"
          disabled={submitting || !name.trim()}
          className="rounded-xl bg-emerald-600 px-4 py-2.5 font-medium text-white shadow-sm hover:bg-emerald-700 disabled:opacity-60"
        >
          {submitting ? 'Adding…' : 'Add category'}
        </button>
      </form>

      {error && (
        <div className="mb-4 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
          {error}
        </div>
      )}
      {loading ? (
        <p className="text-slate-500">Loading…</p>
      ) : (
        <ul className="list-none rounded-xl border border-slate-200 bg-white shadow-sm p-0">
          {categories.map((c) => (
            <li
              key={c.id}
              className="flex items-center justify-between border-b border-slate-100 px-4 py-3 last:border-0"
            >
              <span className="font-medium text-slate-800">{c.name}</span>
              <button
                type="button"
                className="inline-flex rounded-lg border border-slate-300 p-2 text-slate-600 hover:bg-rose-50 hover:text-rose-600"
                onClick={() => handleDelete(c.id)}
                title="Delete"
              >
                <FiTrash2 className="text-base" aria-hidden />
              </button>
            </li>
          ))}
          {categories.length === 0 && (
            <li className="px-4 py-6 italic text-slate-500">No categories yet.</li>
          )}
        </ul>
      )}
    </div>
  )
}
