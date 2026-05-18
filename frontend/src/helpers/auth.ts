import type IUser from '../types/IUser'
import api from './api'


export async function loginAndFetchUser(
  username: string,
  password: string,
): Promise<Record<string, IUser>> {

  const body = new URLSearchParams({
    username: username.trim(),
    password: password.trim(),
  })

  try {
    // POST request to authenticate with Spring Security default login
    await api.post('/auth/login', body, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    })
  } catch (error: any) {
    const message = error.response?.data || error.message
    throw new Error(
      typeof message === 'string' && message.trim()
        ? message
        : 'Invalid username or password',
    )
  }

  //Fetch current user after login
  try {
    const userResponse = await api.get('/users/me')
    const userData = userResponse.data
    console.log('Logged-in user:', userData)
    return userData
  } catch (error: any) {
    throw new Error('Login succeeded but failed to load your profile.')
  }
}
