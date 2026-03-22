import { useState, useEffect } from 'react'
import { FiTarget, FiSave } from 'react-icons/fi'
import * as expensesApi from '../api/expenses'

const BUDGET_KEY = 'money-mgr-monthly-budget'

function getThisMonthRange() {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  return {
    from: `${y}-${m}-01`,
    to: new Date(y, now.getMonth() + 1, 0).toISOString().slice(0, 10),
  }
}

function loadBudget() {
  try {
    const v = localStorage.getItem(BUDGET_KEY)
    return v ? Number(v) : ''
  } catch {
    return ''
  }
}

function saveBudget(value) {
  try {
    if (value === '' || value == null) {
      localStorage.removeItem(BUDGET_KEY)
    } else {
      localStorage.setItem(BUDGET_KEY, String(value))
    }
  } catch {}
}

export default function Budget() {
  const [budget, setBudget] = useState('')
  const [monthTotal, setMonthTotal] = useState(0)
  const [loading, setLoading] = useState(true)
  const [saved, setSaved] = useState(false)
  const { from, to } = getThisMonthRange()
  const monthLabel = new Date().toLocaleString('default', { month: 'long', year: 'numeric' })

  useEffect(() => {
    setBudget(loadBudget())
  }, [])

  useEffect(() => {
    expensesApi
      .getExpenses({ from, to })
      .then((list) => setMonthTotal(list.reduce((s, e) => s + (e.amount || 0), 0)))
      .catch(() => setMonthTotal(0))
      .finally(() => setLoading(false))
  }, [from, to])

  const budgetNum = budget === '' ? 0 : Number(budget)
  const percent = budgetNum > 0 ? Math.min(100, (monthTotal / budgetNum) * 100) : 0
  const isOver = budgetNum > 0 && monthTotal > budgetNum

  const handleSave = () => {
    const v = budget === '' ? '' : String(Number(budget))
    saveBudget(v)
    setSaved(true)
    setTimeout(() => setSaved(false), 2000)
  }

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-slate-800">Budget</h1>

      <section className="mb-8 rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="mb-4 flex items-center gap-2 text-lg font-semibold text-slate-800">
          <FiTarget className="text-emerald-600" aria-hidden /> Monthly budget
        </h2>
        <div className="flex flex-wrap items-end gap-4">
          <label className="flex flex-col gap-1 text-sm font-medium text-slate-600">
            Budget for {monthLabel}
            <input
              type="number"
              step="0.01"
              min="0"
              placeholder="e.g. 5000"
              value={budget}
              onChange={(e) => setBudget(e.target.value)}
              className="max-w-[180px] rounded-lg border-slate-300"
            />
          </label>
          <button
            type="button"
            onClick={handleSave}
            className="inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-4 py-2.5 font-medium text-white shadow-sm hover:bg-emerald-700"
          >
            <FiSave className="text-base" aria-hidden /> {saved ? 'Saved' : 'Save'}
          </button>
        </div>
        <p className="mt-2 text-sm text-slate-500">
          Stored in this device only. Use it to track spending against your limit.
        </p>
      </section>

      <section className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="mb-4 text-lg font-semibold text-slate-800">This month</h2>
        {loading ? (
          <p className="text-slate-500">Loading…</p>
        ) : (
          <>
            <div className="mb-4 flex flex-wrap items-baseline justify-between gap-2">
              <div>
                <span className="text-sm text-slate-500">Spent </span>
                <span className="text-xl font-bold text-rose-600 tabular-nums">
                  {monthTotal.toFixed(2)}
                </span>
              </div>
              {budgetNum > 0 && (
                <div className="text-right">
                  <span className="text-sm text-slate-500">Budget </span>
                  <span className="text-lg font-semibold text-slate-800 tabular-nums">
                    {budgetNum.toFixed(2)}
                  </span>
                </div>
              )}
            </div>
            {budgetNum > 0 ? (
              <>
                <div className="h-3 w-full overflow-hidden rounded-full bg-slate-200">
                  <div
                    className="h-full rounded-full transition-all duration-300"
                    style={{
                      width: `${percent}%`,
                      backgroundColor: isOver ? '#dc2626' : '#059669',
                    }}
                  />
                </div>
                <p className="mt-2 text-sm text-slate-600">
                  {isOver
                    ? `Over budget by ${(monthTotal - budgetNum).toFixed(2)}`
                    : `${(budgetNum - monthTotal).toFixed(2)} left`}
                </p>
              </>
            ) : (
              <p className="text-sm text-slate-500">Set a monthly budget above to see progress.</p>
            )}
          </>
        )}
      </section>
    </div>
  )
}
