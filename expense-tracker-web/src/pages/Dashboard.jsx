import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { FiTrendingDown, FiList, FiPlus, FiArrowRight } from 'react-icons/fi'
import * as expensesApi from '../api/expenses'
import * as categoriesApi from '../api/categories'

function getThisMonthRange() {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  const first = `${y}-${m}-01`
  const last = new Date(y, now.getMonth() + 1, 0)
  const lastStr = last.toISOString().slice(0, 10)
  return { from: first, to: lastStr }
}

export default function Dashboard() {
  const [monthExpenses, setMonthExpenses] = useState([])
  const [recentExpenses, setRecentExpenses] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const { from, to } = getThisMonthRange()

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')
    Promise.all([
      expensesApi.getExpenses({ from, to }),
      expensesApi.getExpenses({ from, to }).then((list) => list.slice(0, 10)),
      categoriesApi.getCategories(),
    ])
      .then(([monthList, recentList, catList]) => {
        if (!cancelled) {
          setMonthExpenses(monthList)
          setRecentExpenses(recentList)
          setCategories(catList)
        }
      })
      .catch((err) => { if (!cancelled) setError(err.message) })
      .finally(() => { if (!cancelled) setLoading(false) })
    return () => { cancelled = true }
  }, [from, to])

  const catMap = Object.fromEntries((categories || []).map((c) => [c.id, c.name]))
  const monthTotal = monthExpenses.reduce((s, e) => s + (e.amount || 0), 0)
  const monthLabel = new Date().toLocaleString('default', { month: 'long', year: 'numeric' })

  if (loading) {
    return (
      <div className="flex min-h-[200px] items-center justify-center text-slate-500">
        Loading…
      </div>
    )
  }

  return (
    <div>
      <header className="mb-6 flex flex-wrap items-center justify-between gap-4">
        <h1 className="text-2xl font-bold text-slate-800">Dashboard</h1>
        <Link
          to="/expenses/new"
          className="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-4 py-2.5 font-medium text-white shadow-sm hover:bg-emerald-700 hover:no-underline"
        >
          <FiPlus className="text-lg" aria-hidden /> Add expense
        </Link>
      </header>

      {error && (
        <div className="mb-4 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
          {error}
        </div>
      )}

      {/* Summary cards */}
      <div className="mb-8 grid gap-4 sm:grid-cols-2">
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
          <div className="flex items-center gap-2 text-slate-500">
            <FiTrendingDown className="text-lg" aria-hidden />
            <span className="text-sm font-medium">Spent this month</span>
          </div>
          <p className="mt-2 text-2xl font-bold text-rose-600 tabular-nums">
            {monthTotal.toFixed(2)}
          </p>
          <p className="mt-0.5 text-sm text-slate-500">{monthLabel}</p>
        </div>
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
          <div className="flex items-center gap-2 text-slate-500">
            <FiList className="text-lg" aria-hidden />
            <span className="text-sm font-medium">Transactions</span>
          </div>
          <p className="mt-2 text-2xl font-bold text-slate-800 tabular-nums">
            {monthExpenses.length}
          </p>
          <p className="mt-0.5 text-sm text-slate-500">This month</p>
        </div>
      </div>

      {/* Recent transactions */}
      <section className="rounded-xl border border-slate-200 bg-white shadow-sm">
        <div className="flex items-center justify-between border-b border-slate-200 px-4 py-3">
          <h2 className="text-lg font-semibold text-slate-800">Recent transactions</h2>
          <Link
            to="/transactions"
            className="inline-flex items-center gap-1 text-sm font-medium text-emerald-600 hover:text-emerald-700 hover:no-underline"
          >
            View all <FiArrowRight className="text-sm" aria-hidden />
          </Link>
        </div>
        <div className="divide-y divide-slate-100">
          {recentExpenses.length === 0 ? (
            <div className="px-4 py-8 text-center text-slate-500">
              No transactions this month. Add an expense to get started.
            </div>
          ) : (
            recentExpenses.map((e) => (
              <Link
                key={e.id}
                to={`/expenses/${e.id}/edit`}
                className="flex items-center justify-between px-4 py-3 hover:bg-slate-50/80"
              >
                <div className="min-w-0">
                  <p className="font-medium text-slate-800 truncate">{e.title}</p>
                  <p className="text-sm text-slate-500">
                    {e.expenseDate} · {catMap[e.categoryId] ?? '—'}
                  </p>
                </div>
                <span className="ml-3 shrink-0 tabular-nums font-medium text-rose-600">
                  − {Number(e.amount).toFixed(2)}
                </span>
              </Link>
            ))
          )}
        </div>
      </section>
    </div>
  )
}
