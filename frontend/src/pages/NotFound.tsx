import { Link } from 'react-router-dom'
import { ArrowLeft, XCircle } from 'lucide-react'

function NotFound() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center px-4 py-8">
      <div className="w-full max-w-2xl rounded-4xl border border-slate-800 bg-slate-900/95 p-10 shadow-2xl shadow-slate-950/30">
        <div className="flex items-center gap-4 text-cyan-400">
          <XCircle className="h-10 w-10" />
          <div>
            <p className="text-sm uppercase tracking-[0.3em] text-cyan-300/80">Page not found</p>
            <h1 className="mt-2 text-5xl font-semibold tracking-tight text-slate-100">404</h1>
          </div>
        </div>

        <p className="mt-6 max-w-xl text-base leading-7 text-slate-400">
          The page you are looking for doesn’t exist or has been moved. Use the button below to return to the login screen.
        </p>

        <div className="mt-8 flex flex-wrap gap-3">
          <Link
            to="/login"
            className="inline-flex items-center gap-2 rounded-2xl bg-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400"
          >
            <ArrowLeft className="h-4 w-4" />
            Back to login
          </Link>
        </div>
      </div>
    </div>
  )
}

export default NotFound
