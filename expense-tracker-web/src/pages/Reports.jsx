import { useState, useEffect } from 'react'
import * as reportsApi from '../api/reports'

const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

function Bar({ value, max, label, showValue = true }) {
  const pct = max > 0 ? (value / max) * 100 : 0
  return (
    <div className="flex items-center gap-3 py-1.5">
      <span className="w-9 shrink-0 text-sm text-slate-600">{label}</span>
      <div className="min-w-0 flex-1">
        <div className="h-6 w-full overflow-hidden rounded-md bg-slate-200">
          <div
            className="h-full rounded-md bg-emerald-500 transition-all duration-300"
            style={{ width: `${pct}%` }}
          />
        </div>
      </div>
      {showValue && (
        <span className="w-16 shrink-0 text-right text-sm font-medium tabular-nums text-slate-700">
          {Number(value).toFixed(2)}
        </span>
      )}
    </div>
  )
}

export default function Reports() {
  const currentYear = new Date().getFullYear()
  const [year, setYear] = useState(currentYear)
  const [from, setFrom] = useState('')
  const [to, setTo] = useState('')
  const [monthly, setMonthly] = useState([])
  const [breakdown, setBreakdown] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [breakdownLoaded, setBreakdownLoaded] = useState(false)

  const loadMonthly = async () => {
    setLoading(true)
    setError('')
    try {
      const data = await reportsApi.getMonthlyTotals(year)
      setMonthly(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err.message)
      setMonthly([])
    } finally {
      setLoading(false)
    }
  }

  const loadBreakdown = async () => {
    if (!from || !to) return
    setError('')
    setBreakdownLoaded(true)
    try {
      const data = await reportsApi.getCategoryBreakdown(from, to)
      setBreakdown(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err.message)
      setBreakdown([])
    }
  }

  useEffect(() => {
    loadMonthly()
  }, [year])

  const monthlyTotal = monthly.reduce((s, m) => s + (m.total || 0), 0)
  const maxMonth = Math.max(...monthly.map((m) => m.total || 0), 1)
  const maxCategory = Math.max(...breakdown.map((b) => b.total || 0), 1)

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-slate-800">Reports</h1>

      <section className="mb-8 rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="mb-4 text-lg font-semibold text-slate-800">Monthly totals</h2>
        <label className="mb-4 flex flex-col gap-1 text-sm font-medium text-slate-600">
          Year
          <select
            value={year}
            onChange={(e) => setYear(Number(e.target.value))}
            className="max-w-[140px] rounded-lg border-slate-300"
          >
            {[currentYear - 2, currentYear - 1, currentYear, currentYear + 1].map((y) => (
              <option key={y} value={y}>{y}</option>
            ))}
          </select>
        </label>
        {error && (
          <div className="mb-4 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
            {error}
          </div>
        )}
        {loading ? (
          <p className="text-slate-500">Loading…</p>
        ) : (
          <>
            <p className="mb-4 text-slate-600">
              Year total: <strong className="text-slate-800 tabular-nums">{monthlyTotal.toFixed(2)}</strong>
            </p>
            <div className="space-y-0">
              {MONTHS.map((name, i) => {
                const m = monthly.find((x) => x.month === i + 1)
                const val = m ? (m.total || 0) : 0
                return (
                  <Bar
                    key={name}
                    label={name}
                    value={val}
                    max={maxMonth}
                  />
                )
              })}
            </div>
            {monthly.length === 0 && (
              <p className="italic text-slate-500">No data for this year.</p>
            )}
          </>
        )}
      </section>

      <section className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="mb-4 text-lg font-semibold text-slate-800">Category breakdown</h2>
        <div className="mb-4 flex flex-wrap items-end gap-3">
          <label className="flex flex-col gap-1 text-sm font-medium text-slate-600">
            From
            <input
              type="date"
              value={from}
              onChange={(e) => setFrom(e.target.value)}
              className="min-w-[140px] rounded-lg border-slate-300"
            />
          </label>
          <label className="flex flex-col gap-1 text-sm font-medium text-slate-600">
            To
            <input
              type="date"
              value={to}
              onChange={(e) => setTo(e.target.value)}
              className="min-w-[140px] rounded-lg border-slate-300"
            />
          </label>
          <button
            type="button"
            onClick={loadBreakdown}
            disabled={!from || !to}
            className="rounded-xl bg-emerald-600 px-4 py-2.5 font-medium text-white shadow-sm hover:bg-emerald-700 disabled:opacity-60"
          >
            Load
          </button>
        </div>
        {breakdownLoaded && (
          <div className="space-y-0">
            {breakdown.length === 0 ? (
              <p className="italic text-slate-500">No data for this range.</p>
            ) : (
              breakdown.map((b) => (
                <Bar
                  key={b.categoryId}
                  label={b.categoryName}
                  value={b.total || 0}
                  max={maxCategory}
                />
              ))
            )}
          </div>
        )}
      </section>
    </div>
  )
}
