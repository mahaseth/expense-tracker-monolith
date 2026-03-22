import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import * as expensesApi from '../api/expenses'
import * as categoriesApi from '../api/categories'

export default function ExpenseForm() {
  const { id } = useParams()
  const isEdit = Boolean(id)
  const navigate = useNavigate()
  const [categories, setCategories] = useState([])
  const [form, setForm] = useState({
    title: '',
    amount: '',
    notes: '',
    categoryId: '',
    expenseDate: new Date().toISOString().slice(0, 10),
  })
  const [loading, setLoading] = useState(isEdit)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    categoriesApi.getCategories().then(setCategories).catch(() => {})
  }, [])

  useEffect(() => {
    if (!isEdit) return
    expensesApi
      .getExpense(id)
      .then((e) => {
        setForm({
          title: e.title || '',
          amount: e.amount ?? '',
          notes: e.notes || '',
          categoryId: e.categoryId || '',
          expenseDate: (e.expenseDate || '').slice(0, 10),
        })
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id, isEdit])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    const payload = {
      title: form.title.trim(),
      amount: Number(form.amount),
      notes: (form.notes || '').trim() || undefined,
      categoryId: form.categoryId,
      expenseDate: form.expenseDate,
    }
    try {
      if (isEdit) {
        await expensesApi.updateExpense(id, payload)
      } else {
        await expensesApi.createExpense(payload)
      }
      navigate('/')
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div>
        <p className="text-slate-500">Loading…</p>
      </div>
    )
  }

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-slate-800">{isEdit ? 'Edit expense' : 'New expense'}</h1>
      <form
        onSubmit={handleSubmit}
        className="max-w-md rounded-xl border border-slate-200 bg-white p-6 shadow-sm"
      >
        {error && (
          <div className="mb-4 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
            {error}
          </div>
        )}
        <label className="mb-4 flex flex-col gap-1 text-sm font-medium text-slate-600">
          Title
          <input
            type="text"
            value={form.title}
            onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
            required
            minLength={2}
            maxLength={120}
            className="max-w-xs"
          />
        </label>
        <label className="mb-4 flex flex-col gap-1 text-sm font-medium text-slate-600">
          Amount
          <input
            type="number"
            step="0.01"
            min="0.01"
            value={form.amount}
            onChange={(e) => setForm((f) => ({ ...f, amount: e.target.value }))}
            required
            className="max-w-xs"
          />
        </label>
        <label className="mb-4 flex flex-col gap-1 text-sm font-medium text-slate-600">
          Category
          <select
            value={form.categoryId}
            onChange={(e) => setForm((f) => ({ ...f, categoryId: e.target.value }))}
            required
            className="max-w-xs"
          >
            <option value="">Select category</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </label>
        <label className="mb-4 flex flex-col gap-1 text-sm font-medium text-slate-600">
          Date
          <input
            type="date"
            value={form.expenseDate}
            onChange={(e) => setForm((f) => ({ ...f, expenseDate: e.target.value }))}
            required
            className="max-w-xs"
          />
        </label>
        <label className="mb-4 flex flex-col gap-1 text-sm font-medium text-slate-600">
          Notes
          <textarea
            value={form.notes}
            onChange={(e) => setForm((f) => ({ ...f, notes: e.target.value }))}
            rows={3}
            maxLength={500}
            className="max-w-xs"
          />
        </label>
        <div className="mt-6 flex gap-3">
          <button
            type="submit"
            disabled={submitting}
            className="rounded-xl bg-emerald-600 px-4 py-2.5 font-medium text-white shadow-sm hover:bg-emerald-700 disabled:opacity-60"
          >
            {submitting ? 'Saving…' : isEdit ? 'Update' : 'Create'}
          </button>
          <button
            type="button"
            className="rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-slate-700 hover:bg-slate-50"
            onClick={() => navigate('/')}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}
