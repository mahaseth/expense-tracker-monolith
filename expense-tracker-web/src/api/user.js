import { api } from './client'

export async function getMe() {
  return api('/api/users/me')
}
