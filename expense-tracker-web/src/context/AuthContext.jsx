import { createContext, useContext, useState, useEffect } from 'react'
import * as authApi from '../api/auth'
import * as userApi from '../api/user'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  const loadUser = async () => {
    const token = localStorage.getItem('token')
    if (!token) {
      setUser(null)
      setLoading(false)
      return
    }
    try {
      const me = await userApi.getMe()
      setUser(me)
    } catch {
      localStorage.removeItem('token')
      setUser(null)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadUser()
    const onLogout = () => {
      setUser(null)
    }
    window.addEventListener('auth:logout', onLogout)
    return () => window.removeEventListener('auth:logout', onLogout)
  }, [])

  const login = async (email, password) => {
    const res = await authApi.login(email, password)
    const token = res.token || res.accessToken
    if (token) {
      localStorage.setItem('token', token)
      await loadUser()
    }
    return res
  }

  const register = async (fullName, email, password) => {
    const res = await authApi.register(fullName, email, password)
    const token = res.token || res.accessToken
    if (token) {
      localStorage.setItem('token', token)
      await loadUser()
    }
    return res
  }

  const logout = () => {
    localStorage.removeItem('token')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, refreshUser: loadUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
