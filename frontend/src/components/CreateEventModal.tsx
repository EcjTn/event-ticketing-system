import React, { useState } from 'react'

import { Plus, Trash2, X, Upload } from 'lucide-react'

import { Button } from './Button'
import { createEvent } from '../helpers/events'

import type { EventTierType } from '../types/IEventTier'
import type { ICreateEventRequest, ICreateEventTier } from '../types/ICreateEventRequest'

interface CreateEventModalProps {
    isOpen: boolean
    onClose: () => void
    onSuccess: () => void
}

const TIER_OPTIONS: EventTierType[] = ['GENERAL', 'FLOOR', 'VIP']

export default function CreateEventModal({ isOpen, onClose, onSuccess }: CreateEventModalProps) {
    const [name, setName] = useState('')
    const [date, setDate] = useState('')
    const [venue, setVenue] = useState('')
    const [description, setDescription] = useState('')
    const [image, setImage] = useState<File | null>(null)
    const [tiers, setTiers] = useState<ICreateEventTier[]>([
        { tier: 'GENERAL', price: 50, quantity: 100 }
    ])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    if (!isOpen) return null

    const handleAddTier = () => {
        const used = tiers.map((t) => t.tier)
        const available = TIER_OPTIONS.find((opt) => !used.includes(opt)) || 'GENERAL'
        setTiers([...tiers, { tier: available, price: 0, quantity: 50 }])
    }

    const handleRemoveTier = (index: number) => {
        setTiers(tiers.filter((_, i) => i !== index))
    }

    const handleTierChange = (index: number, field: keyof ICreateEventTier, value: string | number) => {
        const updated = [...tiers]
        updated[index] = {
            ...updated[index],
            [field]: value
        }
        setTiers(updated)
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError(null)

        if (!name.trim()) {
            setError('Event name is required')
            return
        }
        if (!date) {
            setError('Date is required')
            return
        }
        if (!venue.trim()) {
            setError('Venue is required')
            return
        }
        if (!image) {
            setError('Event image is required')
            return
        }
        if (tiers.length === 0) {
            setError('At least one event tier is required')
            return
        }

        const payload: ICreateEventRequest = {
            name,
            date,
            venue,
            description,
            tiers: tiers.map(t => ({
                tier: t.tier,
                price: Number(t.price),
                quantity: Number(t.quantity)
            }))
        }

        setLoading(true)
        try {
            await createEvent(payload, image)
            onSuccess()
            onClose()
        } catch (err: any) {
            const msg = err.response?.data?.message || err.message || 'Failed to create event'
            setError(msg)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4 overflow-y-auto">
            <div className="bg-navy-card border border-navy-border rounded-2xl w-full max-w-2xl p-6 sm:p-8 shadow-2xl relative my-8">
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-slate-400 hover:text-white transition-colors"
                    type="button"
                >
                    <X className="w-6 h-6" />
                </button>

                <h2 className="text-2xl font-bold text-mist mb-6">Create New Event</h2>

                {error && (
                    <div className="mb-4 p-3 bg-red-900/30 border border-red-500/50 text-red-200 rounded-xl text-sm">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label className="block text-sm font-semibold text-slate-300 mb-1">
                            Event Name *
                        </label>
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="e.g. Summer Music Festival"
                            className="w-full px-4 py-2.5 bg-navy-bg border border-navy-border rounded-xl text-slate-200 focus:outline-none focus:border-mist transition-colors text-sm"
                            required
                        />
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-semibold text-slate-300 mb-1">
                                Date *
                            </label>
                            <input
                                type="date"
                                value={date}
                                onChange={(e) => setDate(e.target.value)}
                                className="w-full px-4 py-2.5 bg-navy-bg border border-navy-border rounded-xl text-slate-200 focus:outline-none focus:border-mist transition-colors text-sm"
                                required
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-semibold text-slate-300 mb-1">
                                Venue *
                            </label>
                            <input
                                type="text"
                                value={venue}
                                onChange={(e) => setVenue(e.target.value)}
                                placeholder="e.g. Grand Arena, NY"
                                className="w-full px-4 py-2.5 bg-navy-bg border border-navy-border rounded-xl text-slate-200 focus:outline-none focus:border-mist transition-colors text-sm"
                                required
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-slate-300 mb-1">
                            Description
                        </label>
                        <textarea
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            placeholder="Enter event details..."
                            rows={3}
                            className="w-full px-4 py-2.5 bg-navy-bg border border-navy-border rounded-xl text-slate-200 focus:outline-none focus:border-mist transition-colors text-sm resize-none"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-slate-300 mb-1">
                            Event Banner Image *
                        </label>
                        <div className="flex items-center gap-3">
                            <label className="flex items-center justify-center gap-2 px-4 py-2.5 bg-navy-bg border border-navy-border rounded-xl cursor-pointer hover:border-mist transition-colors text-sm text-slate-300">
                                <Upload className="w-4 h-4 text-mist" />
                                <span>{image ? image.name : 'Choose Image File'}</span>
                                <input
                                    type="file"
                                    accept="image/*"
                                    onChange={(e) => setImage(e.target.files?.[0] || null)}
                                    className="hidden"
                                />
                            </label>
                        </div>
                    </div>

                    <div>
                        <div className="flex items-center justify-between mb-2">
                            <label className="block text-sm font-semibold text-slate-300">
                                Ticket Tiers (Enums: GENERAL, FLOOR, VIP) *
                            </label>
                            {tiers.length < TIER_OPTIONS.length && (
                                <button
                                    type="button"
                                    onClick={handleAddTier}
                                    className="flex items-center text-xs text-mist hover:text-mist-hover font-semibold transition-colors"
                                >
                                    <Plus className="w-3.5 h-3.5 mr-1" /> Add Tier
                                </button>
                            )}
                        </div>

                        <div className="space-y-3">
                            {tiers.map((tierItem, index) => (
                                <div
                                    key={index}
                                    className="flex flex-wrap sm:flex-nowrap items-center gap-3 bg-navy-bg p-3 border border-navy-border rounded-xl"
                                >
                                    <div className="w-full sm:w-1/3">
                                        <select
                                            value={tierItem.tier}
                                            onChange={(e) => handleTierChange(index, 'tier', e.target.value as EventTierType)}
                                            className="w-full px-3 py-2 bg-navy-card border border-navy-border rounded-lg text-slate-200 text-xs focus:outline-none focus:border-mist"
                                        >
                                            {TIER_OPTIONS.map((opt) => (
                                                <option key={opt} value={opt}>
                                                    {opt}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="w-1/2 sm:w-1/3">
                                        <input
                                            type="number"
                                            min="0"
                                            step="0.01"
                                            placeholder="Price"
                                            value={tierItem.price}
                                            onChange={(e) => handleTierChange(index, 'price', parseFloat(e.target.value) || 0)}
                                            className="w-full px-3 py-2 bg-navy-card border border-navy-border rounded-lg text-slate-200 text-xs focus:outline-none focus:border-mist"
                                        />
                                    </div>
                                    <div className="w-1/2 sm:w-1/3">
                                        <input
                                            type="number"
                                            min="1"
                                            placeholder="Quantity"
                                            value={tierItem.quantity}
                                            onChange={(e) => handleTierChange(index, 'quantity', parseInt(e.target.value) || 0)}
                                            className="w-full px-3 py-2 bg-navy-card border border-navy-border rounded-lg text-slate-200 text-xs focus:outline-none focus:border-mist"
                                        />
                                    </div>
                                    {tiers.length > 1 && (
                                        <button
                                            type="button"
                                            onClick={() => handleRemoveTier(index)}
                                            className="text-slate-500 hover:text-red-400 transition-colors p-1"
                                        >
                                            <Trash2 className="w-4 h-4" />
                                        </button>
                                    )}
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="pt-4 flex gap-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="w-1/2 py-3 bg-navy-bg hover:bg-navy-border text-slate-300 rounded-2xl text-sm font-semibold transition-colors"
                        >
                            Cancel
                        </button>
                        <Button type="submit" isLoading={loading} className="w-1/2">
                            Create Event
                        </Button>
                    </div>
                </form>
            </div>
        </div>
    )
}
