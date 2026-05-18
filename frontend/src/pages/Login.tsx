import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Lock, User, ArrowRight } from 'lucide-react'
import { Button } from '../components/Button'
import { loginAndFetchUser } from '../helpers/auth'

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

    try {
      await loginAndFetchUser(username, password)
      navigate('/events')
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'Unable to connect to the server. Please try again.',
      )
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4 py-8">
      <div className="w-full max-w-md rounded-3xl border border-navy-border bg-navy-card p-8 shadow-2xl shadow-black/40">
        <div className="mb-8 text-center">
          <div className="mx-auto mb-4 inline-flex h-16 w-16 items-center justify-center rounded-3xl bg-mist text-navy-bg">
            <ArrowRight className="h-8 w-8" />
          </div>
          <h1 className="text-3xl font-semibold tracking-tight">Login to your account</h1>
          <p className="mt-3 text-sm text-slate-400">Enter your username and password to continue.</p>
        </div>

        <form className="space-y-6" onSubmit={handleSubmit}>
          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-300" htmlFor="username">Username</label>
            <div className="flex items-center gap-3 rounded-2xl border border-navy-border bg-navy-bg px-4 py-3 focus-within:border-mist focus-within:ring-1 focus-within:ring-mist">
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
            <div className="flex items-center gap-3 rounded-2xl border border-navy-border bg-navy-bg px-4 py-3 focus-within:border-mist focus-within:ring-1 focus-within:ring-mist">
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

          <Button type="submit" isLoading={isSubmitting}>
            Login
          </Button>
        </form>
      </div>
    </div>
  )
}

export default Login
