const getBaseUrl = () => {
  const url = import.meta.env.VITE_API_URL
  if (url) return url.replace(/\/$/, '')
  return '' // use relative URLs so Vite proxy works in dev
}

const getToken = () => localStorage.getItem('token')

export async function api(path, options = {}) {
  const base = getBaseUrl()
  const url = path.startsWith('http') ? path : `${base}${path}`
  const token = getToken()
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  }
  if (token) headers.Authorization = `Bearer ${token}`

  const res = await fetch(url, { ...options, headers })
  if (res.status === 401) {
    localStorage.removeItem('token')
    window.dispatchEvent(new Event('auth:logout'))
    throw new Error('Unauthorized')
  }
  if (!res.ok) {
    const text = await res.text()
    let message = `Request failed (${res.status})`
    try {
      const j = JSON.parse(text)
      if (j.message) message = j.message
      else if (j.error) message = j.error
      else if (Array.isArray(j.errors) && j.errors.length > 0)
        message = j.errors.map((e) => e.defaultMessage || e.message || e).join(', ')
      else if (Array.isArray(j.details) && j.details.length > 0)
        message = j.details.map((d) => d.message || d.defaultMessage || d).join(', ')
    } catch (_) {
      if (text) message = text
    }
    throw new Error(message)
  }
  if (res.status === 204) return null
  return res.json()
}
