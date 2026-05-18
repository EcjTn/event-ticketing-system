import { Link } from 'react-router-dom'
import { ArrowLeft, XCircle } from 'lucide-react'

function NotFound() {
  return (
    <div className="min-h-screen bg-navy-bg text-slate-100 flex items-center justify-center px-4 py-8">
      <div className="w-full max-w-2xl rounded-4xl border border-navy-border bg-navy-card p-10 shadow-2xl shadow-black/40">
        <div className="flex items-center gap-4 text-mist">
          <XCircle className="h-10 w-10 text-mist animate-pulse" />
          <div>
            <p className="text-sm uppercase tracking-[0.3em] text-mist/85 font-semibold">Page not found</p>
            <h1 className="mt-2 text-5xl font-extrabold tracking-tight text-slate-100">404</h1>
          </div>
        </div>

        <p className="mt-6 max-w-xl text-base leading-7 text-slate-400">
          The page you are looking for doesn’t exist or has been moved. Use the button below to safely return to the login screen.
        </p>

        <div className="mt-8 flex flex-wrap gap-3">
          <Link
            to="/login"
            className="inline-flex items-center gap-2 rounded-2xl bg-mist px-5 py-3 text-sm font-semibold text-navy-bg transition duration-200 hover:bg-mist-hover active:bg-mist-pressed shadow-lg shadow-mist/10"
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
