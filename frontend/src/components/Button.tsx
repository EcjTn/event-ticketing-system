import React from 'react'

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children: React.ReactNode
  isLoading?: boolean
}

//emerald mist color button
export function Button({ children, isLoading, className = '', ...props }: ButtonProps) {
  return (
    <button
      {...props}
      disabled={isLoading || props.disabled}
      className={`inline-flex h-12 w-full items-center justify-center rounded-2xl bg-mist px-4 text-sm font-semibold text-navy-bg transition-colors duration-200 hover:bg-mist-hover active:bg-mist-pressed disabled:cursor-not-allowed disabled:opacity-60 ${className}`}
    >
      {isLoading ? 'Loading...' : children}
    </button>
  )
}
