import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { FiEdit2, FiTrash2, FiPlus } from 'react-icons/fi'
import * as expensesApi from '../api/expenses'
import * as categoriesApi from '../api/categories'

export default function Transactions() {
  const [expenses, setExpenses] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filters, setFilters] = useState({ from: '', to: '', categoryId: '' })

  const load = async () => {
    setLoading(true)
    setError('')
    try {
      const [expList, catList] = await Promise.all([
        expensesApi.getExpenses({
          ...(filters.from && filters.to ? { from: filters.from, to: filters.to } : {}),
          ...(filters.categoryId ? { categoryId: filters.categoryId } : {}),
        }),
        categoriesApi.getCategories(),
      ])
      setExpenses(expList)
      setCategories(catList)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [filters.from, filters.to, filters.categoryId])

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this transaction?')) return
    try {
      await expensesApi.deleteExpense(id)
      setExpenses((prev) => prev.filter((e) => e.id !== id))
    } catch (err) {
      setError(err.message)
    }
  }

  const catMap = Object.fromEntries((categories || []).map((c) => [c.id, c.name]))

  return (
    <div>
      <header className="mb-6 flex flex-wrap items-center justify-between gap-4">
        <h1 className="text-2xl font-bold text-slate-800">Transactions</h1>
        <Link
          to="/expenses/new"
          className="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-4 py-2.5 font-medium text-white shadow-sm hover:bg-emerald-700 hover:no-underline"
        >
          <FiPlus className="text-lg" aria-hidden /> Add expense
        </Link>
      </header>

      <div className="mb-6 flex flex-wrap items-end gap-4 rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
        <label className="flex flex-col gap-1 text-sm font-medium text-slate-600">
          From
          <input
            type="date"
            value={filters.from}
            onChange={(e) => setFilters((f) => ({ ...f, from: e.target.value }))}
            className="min-w-[140px] rounded-lg border-slate-300"
          />
        </label>
        <label className="flex flex-col gap-1 text-sm font-medium text-slate-600">
          To
          <input
            type="date"
            value={filters.to}
            onChange={(e) => setFilters((f) => ({ ...f, to: e.target.value }))}
            className="min-w-[140px] rounded-lg border-slate-300"
          />
        </label>
        <label className="flex flex-col gap-1 text-sm font-medium text-slate-600">
          Category
          <select
            value={filters.categoryId}
            onChange={(e) => setFilters((f) => ({ ...f, categoryId: e.target.value }))}
            className="min-w-[160px] rounded-lg border-slate-300"
          >
            <option value="">All categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </label>
      </div>

      {error && (
        <div className="mb-4 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
          {error}
        </div>
      )}
      {loading ? (
        <p className="text-slate-500">Loading…</p>
      ) : (
        <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-slate-200 bg-slate-50">
                  <th className="px-4 py-3 text-left font-semibold text-slate-600">Date</th>
                  <th className="px-4 py-3 text-left font-semibold text-slate-600">Title</th>
                  <th className="px-4 py-3 text-left font-semibold text-slate-600">Category</th>
                  <th className="px-4 py-3 text-right font-semibold text-slate-600">Amount</th>
                  <th className="px-4 py-3 text-right font-semibold text-slate-600 w-24">Actions</th>
                </tr>
              </thead>
              <tbody>
                {expenses.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="px-4 py-8 text-center text-slate-500">
                      No transactions yet. Add an expense to get started.
                    </td>
                  </tr>
                ) : (
                  expenses.map((e) => (
                    <tr key={e.id} className="border-b border-slate-100 last:border-0 hover:bg-slate-50/50">
                      <td className="px-4 py-3 text-slate-600">{e.expenseDate}</td>
                      <td className="px-4 py-3 font-medium text-slate-800">{e.title}</td>
                      <td className="px-4 py-3 text-slate-600">{catMap[e.categoryId] ?? '—'}</td>
                      <td className="px-4 py-3 text-right tabular-nums font-medium text-rose-600">
                        − {Number(e.amount).toFixed(2)}
                      </td>
                      <td className="px-4 py-3 text-right">
                        <Link
                          to={`/expenses/${e.id}/edit`}
                          className="mr-2 inline-flex rounded-lg border border-slate-300 p-2 text-slate-600 hover:bg-slate-100"
                          title="Edit"
                        >
                          <FiEdit2 className="text-base" aria-hidden />
                        </Link>
                        <button
                          type="button"
                          className="inline-flex rounded-lg border border-slate-300 p-2 text-slate-600 hover:bg-rose-50 hover:text-rose-600"
                          onClick={() => handleDelete(e.id)}
                          title="Delete"
                        >
                          <FiTrash2 className="text-base" aria-hidden />
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}
