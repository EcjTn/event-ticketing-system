import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Lock, User, ArrowRight } from 'lucide-react'

const LOGIN_API = import.meta.env.VITE_LOGIN_API_URL ?? '/login'

function Login() {
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState('')

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setIsSubmitting(true)

    const body = new URLSearchParams({
      username: username.trim(),
      password: password.trim(),
    })

    try {
      const response = await fetch(LOGIN_API, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: body.toString(),
        credentials: 'include',
      })

      if (!response.ok) {
        const message = await response.text()
        setError(message || 'Invalid username or password')
        return
      }

      navigate('/')
    } catch (err) {
      setError('Unable to connect to the server. Please try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center px-4 py-8">
      <div className="w-full max-w-md rounded-3xl border border-slate-800 bg-slate-900/95 p-8 shadow-2xl shadow-slate-950/40">
        <div className="mb-8 text-center">
          <div className="mx-auto mb-4 inline-flex h-16 w-16 items-center justify-center rounded-3xl bg-cyan-500 text-slate-950">
            <ArrowRight className="h-8 w-8" />
          </div>
          <h1 className="text-3xl font-semibold tracking-tight">Login to your account</h1>
          <p className="mt-3 text-sm text-slate-400">Enter your username and password to continue.</p>
        </div>

        <form className="space-y-6" onSubmit={handleSubmit}>
          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-300" htmlFor="username">Username</label>
            <div className="flex items-center gap-3 rounded-2xl border border-slate-800 bg-slate-950/80 px-4 py-3 focus-within:border-cyan-500 focus-within:ring-1 focus-within:ring-cyan-500">
              <User className="h-5 w-5 text-slate-400" />
              <input
                id="username"
                name="username"
                type="text"
                value={username}
                onChange={(event) => setUsername(event.target.value)}
                autoComplete="username"
                required
                className="w-full bg-transparent text-slate-100 outline-none placeholder:text-slate-500"
                placeholder="Username"
              />
            </div>
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-300" htmlFor="password">Password</label>
            <div className="flex items-center gap-3 rounded-2xl border border-slate-800 bg-slate-950/80 px-4 py-3 focus-within:border-cyan-500 focus-within:ring-1 focus-within:ring-cyan-500">
              <Lock className="h-5 w-5 text-slate-400" />
              <input
                id="password"
                name="password"
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                autoComplete="current-password"
                required
                className="w-full bg-transparent text-slate-100 outline-none placeholder:text-slate-500"
                placeholder="Password"
              />
            </div>
          </div>

          {error ? (
            <p className="rounded-2xl border border-rose-500/30 bg-rose-500/10 px-4 py-3 text-sm text-rose-200">{error}</p>
          ) : null}

          <button
            type="submit"
            disabled={isSubmitting}
            className="inline-flex h-12 w-full items-center justify-center rounded-2xl bg-cyan-500 px-4 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isSubmitting ? 'Logging in...' : 'Login'}
          </button>
        </form>
      </div>
    </div>
  )
}

export default Login
