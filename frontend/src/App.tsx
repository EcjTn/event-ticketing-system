import { useEffect } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import Login from './pages/Login'
import NotFound from './pages/NotFound'
import api from './helpers/api'

function App() {

  useEffect(() => {
    api.get('/') //get csrf token
      .catch((error) => {
        console.warn('Failed to fetch initial CSRF token:', error.message)
      })
  }, [])

  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}

export default App
