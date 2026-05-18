import { useEffect, useState } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import Login from './pages/Login'
import NotFound from './pages/NotFound'
import api from './helpers/api'

function App() {
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/events')
      .catch((error) => {
        console.warn('Failed to fetch initial CSRF token:', error.message)
      })
      .finally(() => {
        setLoading(false)
      })
  }, [])

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-navy-bg text-slate-100">
        <div className="flex flex-col items-center gap-4">
          <div className="h-10 w-10 animate-spin rounded-full border-4 border-mist border-t-transparent"></div>
          <p className="text-sm font-medium text-slate-400">Loading session...</p>
        </div>
      </div>
    )
  }

  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}

export default App
