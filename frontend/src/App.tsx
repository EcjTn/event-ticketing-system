import { useEffect } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import Login from './pages/Login'
import NotFound from './pages/NotFound'
import Playground from './pages/Playground'
import api from './helpers/api'

function App() {

  useEffect(() => {
    api.get('/events') //just to get csrf token
      .catch((error) => {
        console.warn('Failed to fetch initial CSRF token:', error.message)
      })
  }, [])

  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="/playground" element={<Playground />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}

export default App
