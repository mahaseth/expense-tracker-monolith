import { Link, NavLink, Outlet, Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { FiPieChart, FiBarChart2, FiDollarSign, FiTarget, FiLogOut } from 'react-icons/fi'

const navLinkClass = ({ isActive }) =>
  `flex min-w-0 flex-col items-center gap-0.5 py-2 text-xs transition-colors md:py-0 md:text-sm ${
    isActive ? 'text-emerald-700 font-semibold' : 'text-gray-600 hover:text-emerald-600'
  }`

export default function Layout() {
  const { user, loading, logout } = useAuth()

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-50 text-slate-500">
        <p>Loading…</p>
      </div>
    )
  }

  if (!user) {
    return <Navigate to="/login" replace />
  }

  return (
    <div className="flex min-h-screen flex-col bg-slate-50">
      {/* Top nav: desktop only */}
      <nav className="hidden border-b border-slate-200 bg-white shadow-sm px-6 py-3 md:flex md:flex-wrap md:items-center md:justify-between md:gap-4">
        <Link to="/" className="text-xl font-bold text-emerald-700 hover:text-emerald-800 hover:no-underline tracking-tight">
          Money Mgr
        </Link>
        <div className="flex items-center gap-6">
          <NavLink to="/" className={navLinkClass}>Dashboard</NavLink>
          <NavLink to="/transactions" className={navLinkClass}>Transactions</NavLink>
          <NavLink to="/categories" className={navLinkClass}>Categories</NavLink>
          <NavLink to="/budget" className={navLinkClass}>Budget</NavLink>
          <NavLink to="/reports" className={navLinkClass}>Reports</NavLink>
          <span className="text-sm text-slate-500">{user.fullName || user.email}</span>
          <button
            type="button"
            className="inline-flex items-center gap-1.5 rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm text-slate-600 hover:bg-slate-50"
            onClick={logout}
          >
            <FiLogOut className="text-base" aria-hidden /> Logout
          </button>
        </div>
      </nav>

      {/* Main content: extra padding-bottom on mobile for bottom nav */}
      <main className="mx-auto w-full max-w-4xl flex-1 p-4 pb-24 md:p-6 md:pb-6">
        <Outlet />
      </main>

      {/* Bottom nav: mobile only */}
      <nav className="fixed inset-x-0 bottom-0 z-50 flex items-center justify-around border-t border-slate-200 bg-white/95 py-2 pb-[env(safe-area-inset-bottom)] shadow-[0_-2px_10px_rgba(0,0,0,0.05)] backdrop-blur-sm md:hidden">
        <NavLink to="/" className={navLinkClass}>
          <FiPieChart className="text-lg" aria-hidden />
          <span>Dashboard</span>
        </NavLink>
        <NavLink to="/transactions" className={navLinkClass}>
          <FiDollarSign className="text-lg" aria-hidden />
          <span>Transactions</span>
        </NavLink>
        <NavLink to="/budget" className={navLinkClass}>
          <FiTarget className="text-lg" aria-hidden />
          <span>Budget</span>
        </NavLink>
        <NavLink to="/reports" className={navLinkClass}>
          <FiBarChart2 className="text-lg" aria-hidden />
          <span>Reports</span>
        </NavLink>
        <button
          type="button"
          onClick={logout}
          className="flex min-w-0 flex-col items-center gap-0.5 py-2 text-xs text-slate-600 hover:text-emerald-600"
        >
          <FiLogOut className="text-lg" aria-hidden />
          <span>Logout</span>
        </button>
      </nav>
    </div>
  )
}
